package com.ec.minecraft.admin

import com.ec.model.ItemNBT
import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

class AdminEnchantmentUI: PaginationUI<Unit>("admin-enchantment") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            rows = 6,
            cols = 9,
            title = "&b[&5系统&b] &6技能附魔书".colorize()
        )
    }

    override val isStaticProps: Boolean = true
    override fun props(player: HumanEntity): PaginationUIProps {
        val enchantments = globalManager.enchantments.getEnchantments()
        val items = mutableListOf<PaginationItem>()

        enchantments.forEach {
            for (level in it.startLevel .. it.maxLevel) {
                val item = ItemStack(Material.ENCHANTED_BOOK)
                item.itemMeta<EnchantmentStorageMeta> {
                    val newLores = lore ?: mutableListOf()
                    newLores.add(it.getDisplayLore(level))
                    lore = newLores.colorize()

                    it.origin?.let { ench ->
                        addStoredEnchant(ench, level, true)
                    }

                    addItemFlags(*ItemFlag.values())
                }

                val itemNbt = ItemNBT("", mutableMapOf(it.id to level))
                globalManager.items.serializeToItem(item, itemNbt)

                items.add(PaginationItem(
                    item = item,
                    click = { event ->
                        event.whoClicked.inventory.addItem(item)
                    }
                ))
            }
        }

        return PaginationUIProps(
            globalManager.component.item(Material.BOOK) {
                it.setDisplayName("&b[&5系统&b] &6附魔咨询".colorize())
                it.lore = arrayListOf(
                    "&7总附魔数 &f- &a${enchantments.size}",
                    "&7总附魔数 in.等级 &f- &a${items.size}"
                ).colorize()
            },
            { items }
        )
    }
}