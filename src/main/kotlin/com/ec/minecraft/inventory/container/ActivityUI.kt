package com.ec.minecraft.inventory.container

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.BannerUtil
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ActivityUI: PaginationUI<Unit>("activity") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6活动列表".colorize()
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

                display.add(it.display)
                display.add(BannerUtil[hour[0].digitToInt()])
                display.add(BannerUtil[hour[1].digitToInt()])
                display.add(BannerUtil.colonBanner)
                display.add(BannerUtil[minute[0].digitToInt()])
                display.add(BannerUtil[minute[1].digitToInt()])
                display.add(ItemStack(Material.WHITE_STAINED_GLASS_PANE))
            }

        return PaginationUIProps(
            info = globalManager.component.playerHead(player as Player) {
                it.setDisplayName("&b[&5系统&b] &6活动咨询".colorize())
            },
            items = {
                display.map { PaginationItem(it) }
            },
        )
    }
}