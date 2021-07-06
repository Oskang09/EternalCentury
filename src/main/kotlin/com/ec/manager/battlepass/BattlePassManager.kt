package com.ec.manager.battlepass

import com.ec.ECCore
import com.ec.config.BattlePassConfig
import com.ec.database.enums.BattlePassType
import com.ec.model.app.BattlePassReward
import com.ec.model.app.Reward
import com.ec.manager.GlobalManager
import com.ec.util.HologramUtil.onTouch
import com.ec.util.StringUtil.toColorized
import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
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
            1 to BattlePassReward(
                reward = Reward(
                    type = "enchantment",
                    enchantments = mapOf(
                        "sharpness" to 5
                    )
                )
            )
        )

        override val premiumRewards = mapOf(
            1 to BattlePassReward(
                reward = Reward(
                    type = "enchantment",
                    enchantments = mapOf(
                        "sharpness" to 5
                    )
                )
            )
        )

    }

    private lateinit var globalManager: GlobalManager
    private lateinit var world: World

    private val holograms = mutableMapOf<String, Array<Hologram?>>()
    private val normalPage = mutableMapOf<String, Int>()
    private val premiumPage = mutableMapOf<String, Int>()

    fun getByPlayer(player: Player) :BattlePassConfig {
        return globalManager.states.getPlayerState(player).battlePass[currentSeason] ?:
            BattlePassConfig(BattlePassType.NORMAL, 1, 0, mutableListOf(), mutableListOf())
    }

    fun addXpToPlayer(player: Player, xp: Int) {
        val bp = getByPlayer(player)
        globalManager.states.updatePlayerState(player) {
            bp.experience += xp
            bp.level = activeBattlePass.levelByXp(xp)
            it.battlePass[currentSeason] = bp
        }

        generateBookInfo(player, holograms[player.name]!![12]!!)
    }

    fun onPurchasePremium(player: Player) {
        val bp = getByPlayer(player)
        globalManager.states.updatePlayerState(player) {
            bp.type = BattlePassType.PREMIUM
            it.battlePass[currentSeason] = bp
        }

        generateBookInfo(player, holograms[player.name]!![12]!!)

        val premiumPageInfo = HologramsAPI.createHologram(ECCore.instance, Location(world, 118.5, 74.5, 33.0))
        val premiumVisibility = premiumPageInfo.visibilityManager
        premiumVisibility.showTo(player)
        premiumVisibility.isVisibleByDefault = false

        generatePageInfo(player, BattlePassType.PREMIUM, premiumPageInfo)

        repeat(6) { i ->
            val location = Location(world, 118.5, 73.0, 28.5 + (i * 2))
            val hologram = HologramsAPI.createHologram(ECCore.instance, location)
            val manager = hologram.visibilityManager
            manager.showTo(player)
            manager.isVisibleByDefault = false

            updateRewardHologram(player, BattlePassType.PREMIUM, i, hologram)

            holograms[player.name]!![i + 6] = hologram
        }
        holograms[player.name]!![14] = premiumPageInfo
    }

    private fun onTouchEvent(player: Player, type: BattlePassType, index: Int) {
        val bp = getByPlayer(player)
        val page = (if (type == BattlePassType.PREMIUM) premiumPage[player.name] else normalPage[player.name])!!
        val level = ((page - 1) * 6) + (index + 1)
        val claimedRewards = if (type == BattlePassType.NORMAL) bp.rewards else bp.premiumRewards

        if (player.name == "iRegalia" && bp.level >= level && !claimedRewards.contains(level)) {
            var reward = activeBattlePass.rewards[level]!!
            if (type == BattlePassType.PREMIUM) {
                reward = activeBattlePass.premiumRewards[level]!!
            }

            globalManager.sendRewardToPlayer(player, reward.reward)
            globalManager.states.updatePlayerState(player) {
                val pass = getByPlayer(player)
                if (type == BattlePassType.NORMAL) {
                    pass.rewards.add(level)
                } else {
                    pass.premiumRewards.add(level)
                }

                it.battlePass[currentSeason] = pass
            }
        }
    }

    private fun updateRewardHologram(player: Player, type: BattlePassType, index: Int, hologram: Hologram) {
        val page = if (type == BattlePassType.NORMAL) {
            normalPage[player.name]
        } else {
            premiumPage[player.name]
        }!!

        val offset = (page - 1) * 6
        val level = offset + (index + 1)
        val bp = getByPlayer(player)
        val reward = activeBattlePass.rewards[offset + index] ?: BattlePassReward(
            display = ItemStack(Material.DIAMOND_SWORD),
            description = listOf("testing 1234"),
        )

        val claimedRewards = if (type == BattlePassType.NORMAL) bp.rewards else bp.premiumRewards
        val displayText = if (bp.level < level) { "&4&l等级不足" }
        else {
            if (!claimedRewards.contains(level)) {
                "&a&l可领取"
            } else {
                "&e&l已领取"
            }
        }

        hologram.clearLines()
        hologram.appendTextLine("&f[&6奖励&f] $displayText".toColorized())
        hologram.appendTextLine("")
        reward.description.forEach { s -> hologram.appendTextLine(s.toColorized())  }
        hologram.appendItemLine(reward.display)
        hologram.onTouch {
            onTouchEvent(it.player!!, type, index)
            updateRewardHologram(it.player!!, type, index, hologram)
        }
    }

    private fun generateBookInfo(player: Player, hologram: Hologram) {
        val bp = getByPlayer(player)

        hologram.clearLines()
        hologram.appendTextLine("&f[&5系统&f] &a赛季令牌".toColorized())
        hologram.appendTextLine("&e令牌等级 &f- &0${bp.level}".toColorized())
        hologram.appendTextLine("&e令牌经验 &f- &0${bp.experience}".toColorized())
        hologram.appendTextLine("&e令牌类型 &f- &0${if (bp.type == BattlePassType.NORMAL) "普通" else "优质"}".toColorized())
        hologram.appendItemLine(ItemStack(Material.BOOK))
    }

    private fun generatePageInfo(player: Player, type: BattlePassType, hologram: Hologram) {
        val currentPage = (if (type == BattlePassType.NORMAL) normalPage[player.name] else premiumPage[player.name])!!

        val start = ((currentPage - 1) * 6) + 1
        val end = start + 5

        hologram.clearLines()
        hologram.appendTextLine("&f[&5系统&f] &a${if (type == BattlePassType.NORMAL) "普通" else "优质"}令牌".toColorized())
        hologram.appendTextLine("&e当前页面 &f- &0$currentPage".toColorized())
        hologram.appendTextLine("&e当前等级 &f- &0$start - $end".toColorized())
    }

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
        this.world = Bukkit.getWorld("spawn")!!

        val normalNext = HologramsAPI.createHologram(ECCore.instance, Location(world, 103.0, 72.5, 24.5))
        normalNext.visibilityManager.isVisibleByDefault = true
        normalNext.appendTextLine("&f[&5令牌&f] &a下一页".toColorized())
        normalNext.appendItemLine(ItemStack(Material.HOPPER))

        normalNext.onTouch {
            Bukkit.getLogger().info("test next")

            val player = it.player!!
            val bp = getByPlayer(player)
            val current = normalPage[player.name]!!
            if (current >= (bp.level / 6) + 2) return@onTouch

            normalPage[player.name] = current + 1
            generatePageInfo(player, BattlePassType.NORMAL, holograms[player.name]!![13]!!)

            repeat(6) { i ->
                updateRewardHologram(player, BattlePassType.NORMAL, i, holograms[player.name]!![i]!!)
            }
        }

        val normalPrev = HologramsAPI.createHologram(ECCore.instance, Location(world, 116.0, 72.5, 24.5))
        normalPrev.visibilityManager.isVisibleByDefault = true
        normalPrev.appendTextLine("&f[&5令牌&f] &a上一页".toColorized())
        normalPrev.appendItemLine(ItemStack(Material.SOUL_CAMPFIRE))

        normalPrev.onTouch {
            Bukkit.getLogger().info("test prev")

            val player = it.player!!
            val current = normalPage[player.name]!!
            if (current <= 1) return@onTouch

            normalPage[player.name] = current - 1
            generatePageInfo(player, BattlePassType.NORMAL, holograms[player.name]!![13]!!)

            repeat(6) { i ->
                updateRewardHologram(player, BattlePassType.NORMAL, i, holograms[player.name]!![i]!!)
            }
        }

        val premiumNext = HologramsAPI.createHologram(ECCore.instance, Location(world, 118.5, 72.5, 27.0))
        premiumNext.visibilityManager.isVisibleByDefault = true
        premiumNext.appendTextLine("&f[&5令牌&f] &a下一页".toColorized())
        premiumNext.appendItemLine(ItemStack(Material.HOPPER))

        premiumNext.onTouch {
            val player = it.player!!
            val bp = getByPlayer(player)
            if (bp.type != BattlePassType.PREMIUM) return@onTouch

            val current = premiumPage[player.name]!!
            if (current >= (bp.level / 6) + 2) return@onTouch

            premiumPage[player.name] = current + 1
            generatePageInfo(player, BattlePassType.PREMIUM, holograms[player.name]!![14]!!)

            repeat(6) { i ->
                updateRewardHologram(player, BattlePassType.PREMIUM, i, holograms[player.name]!![i + 6]!!)
            }
        }

        val premiumPrev = HologramsAPI.createHologram(ECCore.instance, Location(world, 118.5, 72.5, 40.0))
        premiumPrev.visibilityManager.isVisibleByDefault = true
        premiumPrev.appendTextLine("&f[&5令牌&f] &a上一页".toColorized())
        premiumPrev.appendItemLine(ItemStack(Material.SOUL_CAMPFIRE))

        premiumPrev.onTouch {
            val player = it.player!!
            val bp = getByPlayer(player)
            if (bp.type != BattlePassType.PREMIUM) return@onTouch

            val current = premiumPage[player.name]!!
            if (current <= 1) return@onTouch

            premiumPage[player.name] = current - 1
            generatePageInfo(player, BattlePassType.PREMIUM, holograms[player.name]!![14]!!)

            repeat(6) { i ->
                updateRewardHologram(player, BattlePassType.PREMIUM, i, holograms[player.name]!![i + 6]!!)
            }
        }

        globalManager.events {

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
                        val player = it.player
                        val playerBp = getByPlayer(it.player)

                        globalManager.runInMainThread {
                            val bookInfo = HologramsAPI.createHologram(ECCore.instance, Location(world, 111.5, 72.5, 31.5))
                            val infoVisibility = bookInfo.visibilityManager
                            infoVisibility.showTo(it.player)
                            infoVisibility.isVisibleByDefault = false

                            generateBookInfo(player, bookInfo)
                            hologramList[12] = bookInfo

                            val activePage = ( playerBp.level / 6 )  + 1
                            premiumPage[it.player.name] = activePage
                            normalPage[it.player.name] = activePage

                            val normalPageInfo = HologramsAPI.createHologram(ECCore.instance, Location(world, 110.0, 74.5, 25.5))
                            val normalVisibility = normalPageInfo.visibilityManager
                            normalVisibility.showTo(it.player)
                            normalVisibility.isVisibleByDefault = false
                            generatePageInfo(player, BattlePassType.NORMAL, normalPageInfo)

                            repeat(6) { i ->
                                val location = Location(world, 104.5 + (i * 2), 73.0, 24.5)
                                val hologram = HologramsAPI.createHologram(ECCore.instance, location)
                                val manager = hologram.visibilityManager
                                manager.showTo(it.player)
                                manager.isVisibleByDefault = false

                                updateRewardHologram(it.player, BattlePassType.NORMAL, i, hologram)

                                hologramList[i] = hologram
                            }
                            hologramList[13] = normalPageInfo

                            if (playerBp.type == BattlePassType.PREMIUM) {
                                val premiumPageInfo = HologramsAPI.createHologram(ECCore.instance, Location(world, 118.5, 74.5, 33.0))
                                val premiumVisibility = premiumPageInfo.visibilityManager
                                premiumVisibility.showTo(it.player)
                                premiumVisibility.isVisibleByDefault = false

                                generatePageInfo(player, BattlePassType.PREMIUM, premiumPageInfo)

                                repeat(6) { i ->
                                    val location = Location(world, 118.5, 73.0, 28.5 + (i * 2))
                                    val hologram = HologramsAPI.createHologram(ECCore.instance, location)
                                    val manager = hologram.visibilityManager
                                    manager.showTo(it.player)
                                    manager.isVisibleByDefault = false

                                    updateRewardHologram(it.player, BattlePassType.PREMIUM, i, hologram)

                                    hologramList[i + 6] = hologram
                                }
                                hologramList[14] = premiumPageInfo
                            }
                            holograms[it.player.name] = hologramList
                        }
                    }
                }
        }

    }

}