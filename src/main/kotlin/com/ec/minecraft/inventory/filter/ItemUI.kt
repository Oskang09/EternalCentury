package com.ec.minecraft.inventory.filter

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

class ItemUI: PaginationUI<ItemUI.InputProps>("item-filter") {

    data class InputProps(
        val material: Boolean = true,
        val native: Boolean = true,
        val onSelect: (Material?, String?) -> Unit,
    )

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6分类选项".colorize()
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        return props(player)
    }

    override fun props(player: HumanEntity, props: InputProps?): PaginationUIProps {
        val display: MutableList<PaginationItem> = mutableListOf()

        if (props!!.native) {
            globalManager.items.getItems().forEach {
                val item = globalManager.items.getItemByConfig(it.value)
                display.add(PaginationItem(
                    item = item,
                    click = { _ ->
                        props.onSelect(null, it.key)
                    }
                ))
            }
        }

        if (props.material) {
            Material.values().filter { it != Material.AIR && it.isItem }.forEach {
                display.add(PaginationItem(
                    item = ItemStack(it),
                    click = { _ ->
                        props.onSelect(it, null)
                    }
                ))
            }
        }

        return PaginationUIProps(
            info = globalManager.component.item(Material.ITEM_FRAME) {
                it.setDisplayName("&b[&5系统&b] &6分类选项".colorize())
            },
            display
        )
    }
}