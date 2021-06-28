package com.ec.minecraft.activity

import com.ec.manager.GlobalManager
import com.ec.manager.activity.ActivityAPI
import com.ec.util.StringUtil.toComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.entity.PlayerDeathEvent
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
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hv load end")

        globalManager.message.broadcast("&f末地探险活动已经开始，请到相关NPC进入地狱吧。")
    }

    override fun onEnd() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hv unload end")

        globalManager.message.broadcast("&f末地探险活动已经结束，下次趁早参加吧。")
    }

    override fun onQuit(event: PlayerQuitEvent) {

    }

    override fun onDeath(event: PlayerDeathEvent) {
    }

    override fun onRespawn(event: PlayerRespawnEvent) {
    }

}