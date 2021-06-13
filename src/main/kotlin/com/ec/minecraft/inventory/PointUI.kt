package com.ec.minecraft.inventory

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.PaginationItem
import com.ec.extension.inventory.component.PaginationUI
import com.ec.extension.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class PointUI: PaginationUI("point") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6点数咨询".colorize()
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        val allPoints = globalManager.points.getPoints()
        return PaginationUIProps(
            info = globalManager.component.item(Material.DIAMOND) {
                it.setDisplayName("&b[&5系统&b] &6点数咨询".colorize())
                it.lore = arrayListOf("&7点数数量 &f- &a${allPoints.size}").colorize()
            },
            items = allPoints.map { (name, point) ->
                val playerPoint = globalManager.points.getPointByNameFromPlayer(name, player as Player)
                return@map PaginationItem(item = point.getItemStack(playerPoint))
            }
        )
    }
}