package com.ec.minecraft.activity

import com.ec.ECCore
import com.ec.database.Players
import com.ec.database.ZombieFights
import com.ec.logger.Logger
import com.ec.manager.GlobalManager
import com.ec.manager.activity.ActivityAPI
import com.ec.manager.wallet.WalletManager
import com.ec.model.player.ECPlayerGameState
import com.ec.util.ChanceUtil
import com.ec.util.RandomUtil
import com.ec.util.StringUtil.generateUniqueID
import com.ec.util.StringUtil.toComponent
import com.google.common.util.concurrent.AtomicDouble
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.DayOfWeek
import java.time.Duration

class ZombieFight: ActivityAPI("zombie-fight") {

    // Configure State
    private val world = "spawn"
    private val locationX = -8.0
    private val locationY = 70.0
    private val locationZ = 291.0
    private val customName = "&f[&e活动&f] &a恶人僵尸".toComponent()
    private val bossBar = BossBar.bossBar(customName.append(" &f- &a等级1".toComponent()), 1F, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
    private val mobList = listOf(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
    )

    // Runtime State
    private var entity: Giant? = null
    private var spawnBase = 0
    private var tpBase = 0
    private var hitBase = 0
    private var bossLevel = 1
    private val totalDamageDeal = AtomicDouble()
    private val damages = mutableMapOf<String, AtomicDouble>()

//    override val weekdays = listOf(
//        DayOfWeek.WEDNESDAY,
//        DayOfWeek.SATURDAY
//    )
    override val weekdays = DayOfWeek.values().toList()
    override val startHour = 20
    override val startMinute = 0
    override val duration = Duration.ofMinutes(3)!!
    override lateinit var display: ItemStack

    override fun initialize(globalManager: GlobalManager) {
        super.initialize(globalManager)

        bossBar.addFlag(BossBar.Flag.CREATE_WORLD_FOG)
        display = globalManager.component.item(Material.ZOMBIE_HEAD) { meta ->
            meta.displayName("&f[&e活动&f] &a僵尸恶战".toComponent())
            meta.lore(arrayListOf(
                "&7&l --- &f&l活动内容 &7&l--- ",
                "&f僵尸恶战是多人讨伐活动，怪物无法被击杀但是",
                "&f会根据玩家的伤害以给予奖励，并且怪物会越来越强。",
                "&f只有攻击巨人才会添加伤害，旁边的怪物是负责骚扰玩家。",
                "&7&l --- &f&l活动奖励 &7&l--- ",
                "&f1. &e500 金钱",
                "&f2. &a1 僵尸恶战点数",
                "&7&l --- &f&l排名奖励 &7&l--- ",
                "&f1. &f[&9稀有&f] &f金钱抽奖卷&e（小）",
                "&f2. &f[&9稀有&f] &f附魔书抽奖本&e（小）",
            ).toComponent())
        }
    }

    override fun onStart() {
        super.onStart()
        globalManager.discord.broadcast("&f僵尸恶战活动已经开始，请到相关NPC进入活动吧。")

        val mcWorld = Bukkit.getWorld(world)!!
        val mcLocation = Location(mcWorld, locationX, locationY, locationZ)

        val giant = mcWorld.spawnEntity(mcLocation, EntityType.GIANT) as Giant
        giant.isCustomNameVisible = true
        giant.isGlowing = true
        giant.removeWhenFarAway = false
        giant.customName(customName)
        giant.noDamageTicks = 0
        giant.maximumNoDamageTicks = 0
        giant.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 1000.0

        entity = giant
        globalManager.events {
            disposers.add(
                EntityDamageEvent::class
                    .observable(true, EventPriority.LOWEST)
                    .filter { it.entity is Giant }
                    .filter { it.entity.customName() == customName }
                    .doOnError(Logger.trackError("ZombieFight.EntityDamageEvent", "error occurs in event subscriber"))
                    .subscribe {
                        it.isCancelled = true
                    }
            )

            disposers.add(
                EntityDeathEvent::class
                    .observable(true, EventPriority.LOWEST)
                    .filter { it.entity.hasMetadata("activity") }
                    .subscribe {
                        val metadata = it.entity.getMetadata("activity")
                        if (metadata.size > 0 && metadata[0].asString() == super.id) {
                            it.drops.clear()
                        }
                    }
            )

            disposers.add(
                EntityDamageByEntityEvent::class
                    .observable(false, EventPriority.LOWEST)
                    .filter { it.damager is Player && it.entity is Giant }
                    .filter { it.entity.customName() == customName }
                    .doOnError(Logger.trackError("ZombieFight.EntityDamageByEntityEvent", "error occurs in event subscriber"))
                    .subscribe {
                        it.isCancelled = true

                        hitBase += 1
                        tpBase += 1
                        spawnBase += 1

                        // Rank Up :
                        if (ChanceUtil.increasingChance(15, hitBase)) {
                            hitBase = 0
                            bossLevel += 1
                            bossBar.name(customName.append(" &f- &a等级$bossLevel".toComponent()))
                        }

                        // Teleport :
                        if (ChanceUtil.trueChance(15, tpBase)) {
                            tpBase = 0

                            val location = it.entity.location
                            location.y += 3
                            it.damager.teleportAsync(location)
                        }

                        // Spawning :
                        if (ChanceUtil.trueChance(10, spawnBase)) {
                            spawnBase = 0

                            spawnEntity(it.entity)
                            spawnEntity(it.entity)
                            spawnEntity(it.entity)
                            if (ChanceUtil.defaultChance(50)) {
                                spawnEntity(it.entity)
                                spawnEntity(it.entity)
                            }
                        }

                        globalManager.runOffMainThread {
                            if (damages[it.damager.name] == null) {
                                damages[it.damager.name] = AtomicDouble(0.0)
                            }

                            damages[it.damager.name]!!.getAndAdd(it.finalDamage)
                            totalDamageDeal.addAndGet(it.finalDamage)
                        }
                    }
            )
        }
    }

    private fun spawnEntity(boss: Entity) {
        val entity = boss.world.spawnEntity(
            randomNearbyLocation(boss.location),
            mobList.random()
        ) as LivingEntity


        /*
         * base + ( n_base * ( level / m_base ) )
         *
         * [base]   - Overall base value
         * [n_base] - Multiplier base value
         * [level]  - Level ( value that increasing )
         * [m_base] - Minecraft based base value
         *
         */
        entity.customName("&f[&e活动&f] &a捣蛋鬼".toComponent())
        entity.setMetadata("activity", FixedMetadataValue(ECCore.instance, super.id))
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 15.0 + (5.0 * ( bossLevel / 20 ))
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0 + (5.0 * ( bossLevel / 20 ))
    }

    private fun randomNearbyLocation(location: Location): Location {
        val posRand = RandomUtil.randomInteger(8)
        val negRand = RandomUtil.randomInteger(8)
        location.x += posRand
        location.x -= negRand
        location.y += posRand
        location.y -= negRand
        location.z += posRand
        location.z -= negRand
        return location
    }

    override fun onEnd() {
        super.onEnd()

        Bukkit.getOnlinePlayers().parallelStream().filter {
            val ecPlayer =  globalManager.players.getByPlayer(it.player!!)
            return@filter ecPlayer.gameState == ECPlayerGameState.ACTIVITY && ecPlayer.activityName == super.id
        }.forEach {
            val ecPlayer =  globalManager.players.getByPlayer(it.player!!)
            ecPlayer.gameState = ECPlayerGameState.FREE
            onQuitEvent(it)
        }

        globalManager.discord.broadcast("&f僵尸恶战活动已经结束，下次再来哟。")

        entity?.remove()
        entity = null
        hitBase = 0
        tpBase = 0
        spawnBase = 0
        bossLevel = 1
        totalDamageDeal.set(0.0)

        globalManager.runOffMainThread {
            transaction {
                damages.toList()
                    .sortedBy { (_, value) -> value.get() }
                    .forEachIndexed { index, pair ->
                        ZombieFights.insert {
                            val ecPlayer = globalManager.players.getByPlayerName(pair.first)!!
                            val overallRank = index + 1
                            it[id] = "".generateUniqueID()
                            it[playerId] = ecPlayer[Players.id]
                            it[damage] = pair.second.get()
                            it[rank] = overallRank

                            val endDateTime = endInstant()
                            it[year] = endDateTime.year
                            it[month] = endDateTime.monthValue
                            it[day] = endDateTime.dayOfMonth

                            globalManager.wallets.depositPlayerWallet(pair.first, WalletManager.ACTIVITY_WALLET, 1.0)
                            globalManager.economy.depositPlayer(pair.first, 500.0)
                            if (overallRank <= 3) {
                                globalManager.givePlayerItem(
                                    pair.first,
                                    listOf(
                                        globalManager.items.getItemById("money-pack-1"),
                                        globalManager.items.getItemById("enchantment-crate-1"),
                                    )
                                )
                            }
                        }
                    }
            }
            damages.clear()
        }
    }

    override fun onJoinEvent(player: Player) {
        player.showBossBar(bossBar)
        player.teleport(globalManager.serverConfig.teleports["zf-arena"]!!.location)
    }

    override fun onQuitEvent(player: Player) {
        player.hideBossBar(bossBar)
        player.teleport(globalManager.serverConfig.teleports["old-spawn"]!!.location)
    }

    override fun onQuit(event: PlayerQuitEvent) {
        onQuitEvent(event.player)
    }

    override fun onRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = globalManager.serverConfig.teleports["zf-arena"]!!.location
    }
}