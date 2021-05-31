package com.ec.minecraft.admin

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.UIProvider
import dev.reactant.reactant.extensions.itemMeta
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.content.InventoryContents
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class AdminUI: UIProvider("admin") {

    override fun info(player: Player): UIBase {
        return UIBase(
            rows = 3,
            cols = 9,
            title = "§b[§5系统§b] §6管理控制台"
        )
    }

    override fun init(player: Player, contents: InventoryContents) {
        contents.fillRow(0, ClickableItem.empty(ItemStack(Material.WHITE_STAINED_GLASS_PANE)))
        contents.fillRow(2, ClickableItem.empty(ItemStack(Material.WHITE_STAINED_GLASS_PANE)))
        contents.set(1, 0, ClickableItem.empty(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
        contents.set(1, 8, ClickableItem.empty(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))

        val enchant = ItemStack(Material.ENCHANTED_BOOK)
        enchant.itemMeta<ItemMeta> {
            setDisplayName("§f§l前往 §b[§5系统§b] §6技能附魔书")
        }
        contents.set(1, 1, ClickableItem.of(enchant) {
            globalManager.inventory.displayTo(player, "admin-enchantment")
        })

    }

    override fun update(player: Player, contents: InventoryContents) {
    }

}