package com.ec.minecraft.inventory.container

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.BannerUtil
import com.ec.util.InstantUtil.toMalaysiaReadableTime
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ActivityUI: PaginationUI<Unit>("activity") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6活动列表"
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        val display = mutableListOf<ItemStack>()
        globalManager.activity.getTodayActivities()
            .forEach {
                var hour = it.startHour.toString()
                var minute = it.startMinute.toString()
                if (hour.length == 1) {
                    hour = "0$hour"
                }

                if (minute.length == 1) {
                    minute = "0$minute"
                }

                val clonedDisplay = it.display.clone()
                clonedDisplay.itemMeta<ItemMeta> {
                    val lores = lore() ?: mutableListOf()
                    lores.add("".toComponent())
                    lores.add("&7开放时间 - &f${it.startInstant().toEpochSecond().toMalaysiaReadableTime()}".toComponent())
                    lores.add("&7结束时间 - &f${it.endInstant().toEpochSecond().toMalaysiaReadableTime()}".toComponent())
                    lore(lores)
                }

                display.add(clonedDisplay)
                display.add(BannerUtil[hour[0].digitToInt()])
                display.add(BannerUtil[hour[1].digitToInt()])
                display.add(BannerUtil.colonBanner)
                display.add(BannerUtil[minute[0].digitToInt()])
                display.add(BannerUtil[minute[1].digitToInt()])
                display.add(ItemStack(Material.WHITE_STAINED_GLASS_PANE))
            }

        return PaginationUIProps(
            info = globalManager.component.playerHead(player as Player) {
                it.displayName("&b[&5系统&b] &6活动咨询".toComponent())
            },
            items = {
                display.map { PaginationItem(it) }
            },
        )
    }
}