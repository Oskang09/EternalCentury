package com.ec.minecraft.activity

import com.ec.manager.GlobalManager
import com.ec.manager.activity.ActivityAPI
import com.ec.model.player.ECPlayerGameState
import com.ec.util.StringUtil.toComponent
import me.oska.config.shop.ItemConfig
import okhttp3.internal.wait
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.time.DayOfWeek
import java.time.Duration

class EndDG: ActivityAPI("end-dg") {

    override val weekdays: List<DayOfWeek> = listOf(DayOfWeek.SUNDAY)
    override val startHour: Int = 8
    override val startMinute: Int = 0
    override val duration: Duration = Duration.ofHours(12)
    override lateinit var display: ItemStack

    override fun initialize(globalManager: GlobalManager) {
        super.initialize(globalManager)

        display = globalManager.component.item(Material.END_PORTAL_FRAME) { meta ->
            meta.displayName("&f[&e活动&f] &a末地探险".toComponent())
            meta.lore(arrayListOf(
                "&7&l --- &f&l活动内容 &7&l--- ",
                "&f开放末地探险，玩家可以组队进入末地探险",
                "&f玩家可以到末地使者NPC，付费并进入末地",
            ).toComponent())
        }
    }

    override fun onStart() {
        super.onStart()

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hv load end")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline end 1 &f[&5开放中&f] &7&l末影探险")

        globalManager.discord.broadcast("&f末地探险活动已经开始，请到相关NPC进入地狱吧。")

        globalManager.events {
            disposers.add(
                PlayerJoinEvent::class
                    .observable(false, EventPriority.LOWEST)
                    .subscribe {
                        if (it.player.world.name == "end") {
                            globalManager.players.getByPlayer(it.player).activityName = super.id
                            globalManager.players.getByPlayer(it.player).gameState = ECPlayerGameState.ACTIVITY
                        }
                    }
            )
        }
    }

    override fun onEnd() {
        super.onEnd()

        Bukkit.getWorld("end")!!.players.map { it.teleportAsync(globalManager.serverConfig.teleports["old-spawn"]!!.location).join() }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hv unload end")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline end 1 &f[&5关闭中&f] &7&l末影探险")

        globalManager.discord.broadcast("&f末地探险活动已经结束，下次趁早参加吧。")
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