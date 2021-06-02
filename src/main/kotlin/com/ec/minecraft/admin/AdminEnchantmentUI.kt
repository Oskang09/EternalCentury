package com.ec.minecraft.admin

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.PaginationItem
import com.ec.extension.inventory.component.PaginationUI
import com.ec.extension.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import de.tr7zw.nbtapi.NBTItem
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

class AdminEnchantmentUI: PaginationUI("admin-enchantment") {

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
            for (level in it.getStartLevel() .. it.getMaxLevel()) {
                val item = ItemStack(Material.ENCHANTED_BOOK)
                item.itemMeta<EnchantmentStorageMeta> {
                    val newLores = lore ?: mutableListOf()
                    newLores.add(it.getDisplayLore(item.type, level))
                    lore = newLores.colorize()

                    it.getOrigin()?.let { ench ->
                        addStoredEnchant(ench, level, true)
                    }

                    addItemFlags(*ItemFlag.values())
                }

                val nbt = NBTItem(item)
                nbt.setObject("ec_ench", mutableMapOf(
                    it.id to level
                ))

                items.add(PaginationItem(
                    item = nbt.item,
                    click = { event ->
                        event.whoClicked.inventory.addItem(nbt.item)
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
            items
        )
    }
}