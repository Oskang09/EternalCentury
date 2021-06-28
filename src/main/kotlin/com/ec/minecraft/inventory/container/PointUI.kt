package com.ec.minecraft.inventory.container

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.toComponent
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class PointUI: PaginationUI<Unit>("point") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6点数咨询"
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        val allPoints = globalManager.points.getPoints()
        return PaginationUIProps(
            info = globalManager.component.item(Material.DIAMOND) {
                it.displayName("&b[&5系统&b] &6点数咨询".toComponent())
                it.lore(arrayListOf("&7点数数量 &f- &a${allPoints.size}").toComponent())
            },
            items = {
                allPoints.map { (name, point) ->
                    val playerPoint = globalManager.points.getPointByNameFromPlayer(name, player as Player)
                    return@map PaginationItem(item = point.getItemStack(playerPoint))
                }
            }
        )
    }
}