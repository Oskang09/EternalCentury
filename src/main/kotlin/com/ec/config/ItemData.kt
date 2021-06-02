package com.ec.config

import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

data class ItemData(
    var material: String = Material.AIR.toString(),
    var name: String = "",
    var lore: List<String> = listOf(),
    var amount: Int,
    var enchantments: MutableMap<String, Int> = mutableMapOf(),
) {
}