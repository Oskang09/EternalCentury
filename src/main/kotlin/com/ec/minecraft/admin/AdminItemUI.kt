package com.ec.minecraft.admin

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.PaginationItem
import com.ec.extension.inventory.component.PaginationUI
import com.ec.extension.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class AdminItemUI: PaginationUI<Unit>("admin-item") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            rows = 6,
            cols = 9,
            title = "&b[&5系统&b] &6物品列表".colorize()
        )
    }

    override val isStaticProps: Boolean = true
    override fun props(player: HumanEntity): PaginationUIProps {
        val items = globalManager.items.getItems()
        val views = mutableListOf<PaginationItem>()

        items.forEach {
            val item = globalManager.items.getItemByKey(it.key)
            views.add(PaginationItem(
                item = item,
                click = { event ->
                    event.whoClicked.inventory.addItem(item)
                }
            ))
        }
        return PaginationUIProps(
            globalManager.component.item(Material.ITEM_FRAME) {
                it.setDisplayName("&b[&5系统&b] &6附魔咨询".colorize())
                it.lore = arrayListOf(
                    "&7总物品数 &f- &a${items.size}",
                ).colorize()
            },
            views,
        )
    }

}