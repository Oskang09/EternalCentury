package com.ec.manager.battlepass

import com.ec.ECCore
import com.ec.config.BattlePassConfig
import com.ec.database.enums.BattlePassType
import com.ec.database.model.BattlePassReward
import com.ec.manager.GlobalManager
import com.ec.util.StringUtil.toColorized
import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

@Component
class BattlePassManager: LifeCycleHook {

    private val currentSeason = "season1"

    val activeBattlePass: BattlePass = object : BattlePass(currentSeason) {

        override fun levelByXp(xp: Int): Int {
            return 0
        }

        override val exclusiveEnchantment = listOf(
            ""
        )
        override val exclusiveParticleStyle = listOf(
            ""
        )

        override val exclusiveParticleEffect = listOf(
            ""
        )

        override val rewards = mapOf(
            1 to BattlePassReward()
        )

        override val premiumRewards = mapOf(
            1 to BattlePassReward()
        )

    }

    private lateinit var globalManager: GlobalManager

    private val sharedHolograms = mutableListOf<Hologram>()
    private val rewardLocations = arrayOfNulls<Location>(12)

    private val holograms = mutableMapOf<String, Array<Hologram?>>()
    private val normalPage = mutableMapOf<String, Int>()
    private val premiumPage = mutableMapOf<String, Int>()

    override fun onDisable() {
        sharedHolograms.forEach { it.delete() }
    }

    fun getByPlayer(player: Player) :BattlePassConfig {
        return globalManager.states.getPlayerState(player).battlePass[currentSeason] ?: BattlePassConfig(BattlePassType.PREMIUM, 0, 0, mutableListOf())
    }

    fun addXpToPlayer(player: Player, xp: Int) {
        val config = getByPlayer(player)
        globalManager.states.updatePlayerState(player) {
            it.battlePass[currentSeason]!!.experience += xp
            it.battlePass[currentSeason]!!.level = activeBattlePass.levelByXp(xp)
        }
    }

    fun showRewards(player: Player, type: BattlePassType) {
        val page = if (type == BattlePassType.NORMAL) {
            normalPage[player.name]
        } else {
            premiumPage[player.name]
        }!!

        val rewardOffset = if (type == BattlePassType.NORMAL) 0 else 6
        val offset = (page - 1) * 6
        val bp = getByPlayer(player)
        repeat(6) {
            val level = offset + it
            val location = rewardLocations[rewardOffset + it]!!
            val reward = activeBattlePass.rewards[offset + it] ?: BattlePassReward(
                display = ItemStack(Material.DIAMOND_SWORD),
                description = listOf("testing 1234"),
            )

            val hologram = holograms[player.name]!![rewardOffset + it]!!
            hologram.clearLines()
            reward.description.forEach { s -> hologram.appendTextLine(s.toColorized())  }
            hologram.appendItemLine(reward.display)

            var blockData = if (bp.level < level) {
                Material.RED_WOOL.createBlockData()
            } else {
                if (bp.rewards.contains(level)) {
                    Material.YELLOW_WOOL.createBlockData()
                } else {
                    Material.LIME_WOOL.createBlockData()
                }
            }
            player.sendBlockChange(location, blockData)
        }
    }

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        val world = Bukkit.getWorld("spawn")!!
        val normalPrev = HologramsAPI.createHologram(ECCore.instance, Location(world, 103.0, 72.5, 24.5))
        normalPrev.visibilityManager.isVisibleByDefault = true
        normalPrev.appendTextLine("&f[&5令牌&f] &a下一页".toColorized())
        normalPrev.appendItemLine(ItemStack(Material.HOPPER))
        sharedHolograms.add(normalPrev)

        val normalNext = HologramsAPI.createHologram(ECCore.instance, Location(world, 116.0, 72.5, 24.5))
        normalNext.visibilityManager.isVisibleByDefault = true
        normalNext.appendTextLine("&f[&5令牌&f] &a上一页".toColorized())
        normalNext.appendItemLine(ItemStack(Material.SOUL_CAMPFIRE))
        sharedHolograms.add(normalNext)

        val premiumPrev = HologramsAPI.createHologram(ECCore.instance, Location(world, 118.5, 72.5, 27.0))
        premiumPrev.visibilityManager.isVisibleByDefault = true
        premiumPrev.appendTextLine("&f[&5令牌&f] &a下一页".toColorized())
        premiumPrev.appendItemLine(ItemStack(Material.HOPPER))
        sharedHolograms.add(premiumPrev)

        val premiumNext = HologramsAPI.createHologram(ECCore.instance, Location(world, 118.5, 72.5, 40.0))
        premiumNext.visibilityManager.isVisibleByDefault = true
        premiumNext.appendTextLine("&f[&5令牌&f] &a上一页".toColorized())
        premiumNext.appendItemLine(ItemStack(Material.SOUL_CAMPFIRE))
        sharedHolograms.add(premiumNext)

        (normalPrev.getLine(0) as TouchableLine).setTouchHandler { }

        (normalNext.getLine(0) as TouchableLine).setTouchHandler { }

        (premiumPrev.getLine(0) as TouchableLine).setTouchHandler { }

        (premiumNext.getLine(0) as TouchableLine).setTouchHandler { }

        globalManager.events {

            PlayerInteractEvent::class
                .observable(true, EventPriority.LOWEST)
                .filter { it.clickedBlock != null }
                .filter { rewardLocations.contains(it.clickedBlock!!.location) }
                .filter { it.hand != EquipmentSlot.OFF_HAND}
                .subscribe {
                    it.isCancelled = true

                    val block = it.clickedBlock!!
                    println(block)
                }

            PlayerQuitEvent::class
                .observable(false, EventPriority.HIGHEST)
                .subscribe {
                    holograms.remove(it.player.name)?.forEach { hd -> hd?.delete() }
                }

            PlayerJoinEvent::class
                .observable(false, EventPriority.HIGHEST)
                .subscribe {
                    globalManager.runOffMainThread {
                        val hologramList = arrayOfNulls<Hologram>(15)
                        val playerBp = getByPlayer(it.player)
                        globalManager.runInMainThread {
                            val bookInfo = HologramsAPI.createHologram(ECCore.instance, Location(world, 111.5, 72.0, 31.5))
                            val infoVisibility = bookInfo.visibilityManager
                            infoVisibility.showTo(it.player)
                            infoVisibility.isVisibleByDefault = false

                            bookInfo.appendTextLine("&f[&5系统&f] &a赛季令牌".toColorized())
                            bookInfo.appendTextLine("&e令牌等级 &f- &01".toColorized())
                            bookInfo.appendTextLine("&e令牌类型 &f- &0普通".toColorized())
                            bookInfo.appendItemLine(ItemStack(Material.BOOK))

                            hologramList[12] = bookInfo

                            val activePage = ( playerBp.level / 6 )  + 1
                            val normalPageInfo = HologramsAPI.createHologram(ECCore.instance, Location(world, 110.0, 74.5, 25.5))
                            val normalVisibility = normalPageInfo.visibilityManager
                            normalVisibility.showTo(it.player)
                            normalVisibility.isVisibleByDefault = false

                            normalPageInfo.appendTextLine("&f[&5系统&f] &a普通令牌".toColorized())
                            normalPageInfo.appendTextLine("&e当前页面 &f- &01".toColorized())
                            normalPageInfo.appendTextLine("&e当前等级 &f- &01 - 6".toColorized())

                            repeat(6) { i ->
                                val location = Location(world, 104.5 + (i * 2), 73.0, 24.5)
                                val hologram = HologramsAPI.createHologram(ECCore.instance, location)
                                val manager = hologram.visibilityManager
                                manager.showTo(it.player)
                                manager.isVisibleByDefault = false

                                hologramList[i] = hologram
                                val rewardLocation = Location(world, 104.0 + (i * 2), 71.0, 24.0)
                                rewardLocations[i] = rewardLocation
                            }
                            normalPage[it.player.name] = activePage
                            hologramList[13] = normalPageInfo

                            if (playerBp.type == BattlePassType.PREMIUM) {
                                val premiumPageInfo = HologramsAPI.createHologram(ECCore.instance, Location(world, 118.5, 74.5, 33.0))
                                val premiumVisibility = premiumPageInfo.visibilityManager
                                premiumVisibility.showTo(it.player)
                                premiumVisibility.isVisibleByDefault = false

                                premiumPageInfo.appendTextLine("&f[&5系统&f] &a优质令牌".toColorized())
                                premiumPageInfo.appendTextLine("&e当前页面 &f- &01".toColorized())
                                premiumPageInfo.appendTextLine("&e当前等级 &f- &01 - 6".toColorized())

                                repeat(6) { i ->
                                    val location = Location(world, 118.5, 73.0, 28.5 + (i * 2))
                                    val hologram = HologramsAPI.createHologram(ECCore.instance, location)
                                    val manager = hologram.visibilityManager
                                    manager.showTo(it.player)
                                    manager.isVisibleByDefault = false

                                    hologramList[i + 6] = hologram
                                    val rewardLocation = Location(world, 118.0, 71.0, 28.0 + (i * 2))
                                    rewardLocations[i + 6] = rewardLocation
                                }
                                premiumPage[it.player.name] = activePage
                                hologramList[14] = premiumPageInfo
                            }

                            holograms[it.player.name] = hologramList

                            showRewards(it.player, BattlePassType.NORMAL)
                            if (playerBp.type == BattlePassType.PREMIUM) {
                                showRewards(it.player, BattlePassType.PREMIUM)
                            }
                        }
                    }
                }
        }

    }

}