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
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hv load nether")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rtp addworld nether true false ")

        globalManager.message.broadcast("&f地狱旅途活动已经开始，请到相关NPC进入地狱吧。")
    }

    override fun onEnd() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hv unload nether")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rtp remove world nether")

        globalManager.message.broadcast("&f地狱旅途活动已经结束，下次趁早参加吧。")
    }

    override fun onQuit(event: PlayerQuitEvent) {

    }

    override fun onDeath(event: PlayerDeathEvent) {

    }

    override fun onRespawn(event: PlayerRespawnEvent) {

    }

}