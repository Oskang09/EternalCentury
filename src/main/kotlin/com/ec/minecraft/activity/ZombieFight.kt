package com.ec.minecraft.activity

import com.ec.database.Players
import com.ec.database.ZombieFights
import com.ec.manager.GlobalManager
import com.ec.manager.activity.ActivityAPI
import com.ec.manager.wallet.WalletManager
import com.ec.util.ChanceUtil
import com.ec.util.RandomUtil
import com.ec.util.StringUtil.generateUniqueID
import com.ec.util.StringUtil.toComponent
import com.google.common.util.concurrent.AtomicDouble
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.entity.*
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.DayOfWeek
import java.time.Duration

class ZombieFight: ActivityAPI("zombie-fight") {

    // Configure State
    private val bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID)
    private val world = ""
    private val locationX = 0.0
    private val locationY = 0.0
    private val locationZ = 0.0
    private val customName = "&f[&e活动&f] &a恶人僵尸".toComponent()
    private val mobList = listOf(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
    )

    // Runtime State
    private var entity: Giant? = null
    private var bossBarScheduler = ""
    private var spawnBase = 0
    private var tpBase = 0
    private var hitBase = 0
    private var bossLevel = 1
    private val totalDamageDeal = AtomicDouble()
    private val listeners = mutableListOf<Disposable>()
    private val damages = mutableMapOf<String, AtomicDouble>()

    override val weekdays = listOf(
        DayOfWeek.WEDNESDAY,
        DayOfWeek.SATURDAY
    )
    override val startHour = 20
    override val startMinute = 0
    override val duration = Duration.ofMinutes(30)!!
    override lateinit var display: ItemStack

    override fun initialize(globalManager: GlobalManager) {
        super.initialize(globalManager)

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
        bossBarScheduler = globalManager.states.continuousTask(1) {
            bossBar.players.clear()
            bossBar.addFlag(BarFlag.CREATE_FOG)
            bossBar.setTitle("$customName &f- &a等级$bossLevel")
            bossBar.players.addAll(
                giant.getNearbyEntities(200.0, 200.0, 200.0)
                    .filterIsInstance<Player>()
                    .toTypedArray()
            )
        }

        globalManager.events {
            listeners.add(
                EntityDamageEvent::class
                    .observable(true, EventPriority.LOWEST)
                    .filter { it.entity is Giant }
                    .filter { it.entity.customName() == customName }
                    .subscribe {
                        it.isCancelled = true
                    }
            )
            listeners.add(
                EntityDamageByEntityEvent::class
                    .observable(false, EventPriority.LOWEST)
                    .filter { it.damager is Player && it.entity is Giant }
                    .filter { it.entity.customName() == customName }
                    .subscribe {
                        it.isCancelled = true

                        hitBase += 1
                        tpBase += 1
                        spawnBase += 1

                        // Rank Up :
                        if (ChanceUtil.increasingChance(15, hitBase)) {
                            hitBase = 0
                            bossLevel += 1
                        }

                        // Teleport :
                        if (ChanceUtil.trueChance(20, tpBase)) {
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
        globalManager.states.disposeTask(bossBarScheduler)
        entity?.remove()
        listeners.forEach { it.dispose() }

        entity = null
        hitBase = 0
        tpBase = 0
        spawnBase = 0
        bossLevel = 1
        totalDamageDeal.set(0.0)
        listeners.clear()

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
                                        globalManager.items.getItemByKey("money-pack-1"),
                                        globalManager.items.getItemByKey("enchantment-crate-1"),
                                    )
                                )
                            }
                        }
                    }
            }
            damages.clear()
        }
    }

    override fun onQuit(event: PlayerQuitEvent) {
    }

    override fun onDeath(event: PlayerDeathEvent) {
    }

    override fun onRespawn(event: PlayerRespawnEvent) {
    }
}