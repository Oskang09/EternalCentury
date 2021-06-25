package com.ec.minecraft.ugui

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extensions.itemMeta
import me.oska.module.ItemProvider
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class UguiProvider(private val globalManager: GlobalManager): ItemProvider() {

    override fun getAuthor(): String {
        return "Oska"
    }

    override fun getName(): String {
        return "EternalCentury"
    }

    override fun getVersion(): String {
        return ""
    }

    override fun isSupported() {

    }

    override fun get(config: Map<*, *>): ItemStack {
        val item = when (config["type"] ?: "item") {
            "item" -> {
                val id = config["id"] as String
                globalManager.items.getItemByKey(id)
            }
            "enchantment" -> {
                val enchantments = config["enchantments"] as Map<String, Int>
                globalManager.enchantments.getEnchantedBookByMap(enchantments)
            }
            else -> ItemStack(Material.AIR)
        }

        val extras = config["lore"] as List<String>?
        if (extras != null) {
            item.itemMeta<ItemMeta> {
                val lores = lore ?: mutableListOf()
                lores.addAll(extras)

                lore = lores
            }
        }
        return item
    }

    override fun key(): String {
        return "ec-item"
    }

}