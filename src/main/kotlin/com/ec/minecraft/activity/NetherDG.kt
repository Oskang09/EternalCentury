package com.ec.minecraft.activity

import com.ec.manager.GlobalManager
import com.ec.manager.activity.ActivityAPI
import com.ec.model.player.ECPlayerGameState
import com.ec.util.StringUtil.toComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.time.DayOfWeek
import java.time.Duration

class NetherDG: ActivityAPI("nether-dg") {

    override val weekdays: List<DayOfWeek> = listOf(DayOfWeek.SATURDAY)
    override val startHour: Int = 8
    override val startMinute: Int = 0
    override val duration: Duration = Duration.ofHours(12)
    override lateinit var display: ItemStack

    override fun initialize(globalManager: GlobalManager) {
        super.initialize(globalManager)

        display = globalManager.component.item(Material.NETHER_STAR) { meta ->
            meta.displayName("&f[&e活动&f] &a地狱旅途".toComponent())
            meta.lore(arrayListOf(
                "&7&l --- &f&l活动内容 &7&l--- ",
                "&f开放地狱世界，玩家可以组队进入地狱旅途",
                "&f玩家可以到地狱使者NPC，付费并进入地狱",
            ).toComponent())
        }
    }


    override fun onStart() {
        super.onStart()

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hv load nether")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rtp addworld nether true false ")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline nether 1 &f[&5开放中&f] &c&l地狱探险")

        globalManager.discord.broadcast("&f地狱旅途活动已经开始，请到相关NPC进入地狱吧。")

        globalManager.events {
            disposers.add(
                PlayerJoinEvent::class
                    .observable(false, EventPriority.LOWEST)
                    .subscribe {
                        if (it.player.world.name == "nether") {
                            globalManager.players.getByPlayer(it.player).activityName = super.id
                            globalManager.players.getByPlayer(it.player).gameState = ECPlayerGameState.ACTIVITY
                        }
                    }
            )
        }
    }

    override fun onEnd() {
        super.onEnd()

        Bukkit.getWorld("nether")!!.players.map { it.teleportAsync(globalManager.serverConfig.teleports["old-spawn"]!!.location).join() }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hv unload nether")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rtp remove world nether")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline nether 1 &f[&5关闭中&f] &c&l地狱探险")

        globalManager.discord.broadcast("&f地狱旅途活动已经结束，下次趁早参加吧。")
    }

    override fun onDeath(event: PlayerDeathEvent) {
        val death = event.entity

        globalManager.players.getByPlayer(death).gameState = ECPlayerGameState.FREE
        globalManager.players.getByPlayer(death).activityName = ""
    }

    override fun onRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = globalManager.serverConfig.teleports["old-spawn"]!!.location
    }

}