package com.ec.minecraft.admin

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.toComponent
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class AdminItemUI: PaginationUI<Unit>("admin-item") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            rows = 6,
            cols = 9,
            title = "&b[&5系统&b] &6物品列表"
        )
    }

    override val isStaticProps: Boolean = true
    override fun props(player: HumanEntity): PaginationUIProps {
        val items = globalManager.items.getItems()
        val views = mutableListOf<PaginationItem>()

        items.forEach {
            val item = globalManager.items.getItemById(it.key)
            views.add(PaginationItem(
                item = item,
                click = { event ->
                    event.whoClicked.inventory.addItem(item)
                }
            ))
        }
        return PaginationUIProps(
            globalManager.component.item(Material.ITEM_FRAME) {
                it.displayName("&b[&5系统&b] &6附魔咨询".toComponent())
                it.lore(arrayListOf(
                    "&7总物品数 &f- &a${items.size}",
                ).toComponent())
            },
            { views }
        )
    }

}