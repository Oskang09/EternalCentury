package com.ec.minecraft.admin

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.UIProvider
import de.tr7zw.changeme.nbtapi.NBTItem
import dev.reactant.reactant.extensions.itemMeta
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.SlotIterator
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class AdminEnchantmentUI: UIProvider("admin-enchantment") {

    override fun info(player: Player): UIBase {
        return UIBase(
            rows = 6,
            cols = 9,
            title = "§b[§5系统§b] §6技能附魔书"
        )
    }

    override fun init(player: Player, contents: InventoryContents) {
        val paginator = contents.pagination()
        val next = paginator.next().page
        val prev = paginator.previous().page

        val enchantments = globalManager.enchantments.getEnchantments()
        val items = mutableListOf<ClickableItem>()

        enchantments.forEach {
            for (level in it.getStartLevel() .. it.getMaxLevel()) {
                val item = ItemStack(Material.ENCHANTED_BOOK)
                item.itemMeta<ItemMeta> {
                    val newLores = lore ?: mutableListOf()
                    newLores.add("§7 --- 附魔咨询 --- ")
                    newLores.add(it.getDisplayLore(level))
                    lore = newLores

                    addItemFlags(*ItemFlag.values())
                }

                val nbt = NBTItem(item)
                nbt.setObject("ec_ench", mutableMapOf(
                    it.id to 1
                ))

                items.add(ClickableItem.of(nbt.item) { event ->
                    event.whoClicked.inventory.addItem(item)
                })
            }
        }

        paginator.setItems(*items.toTypedArray())
        paginator.setItemsPerPage(42)
        paginator.addToIterator(contents.newIterator(SlotIterator.Type.VERTICAL, 0, 2))

        contents.set(
            0, 0,
            ClickableItem.empty(globalManager.component.playerHead(player) {
                it.setDisplayName("§b[§5系统§b] §6附魔咨询")
                it.lore = arrayListOf(
                    "§7总附魔数 §f- §a${enchantments.size}",
                    "§7总附魔数 ex.等级 §f- §a${items.size}"
                )
            })
        )

        contents.set(1, 0, ClickableItem.empty(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
        contents.fillColumn(1, ClickableItem.empty(ItemStack(Material.WHITE_STAINED_GLASS_PANE)))
        if (!paginator.isFirst) {
            contents.set(2, 0, ClickableItem.of(globalManager.component.arrowPrevious(prev)) {
                this.displayTo(player, prev)
            })
        }

        if (!paginator.isLast) {
            contents.set(6, 0, ClickableItem.of(globalManager.component.arrowNext(next)) {
                this.displayTo(player, next)
            })
        }
    }

    override fun update(player: Player, contents: InventoryContents) {

    }
}