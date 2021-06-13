package com.ec.config

import org.bukkit.Material

data class ItemConfig(
    var material: String = Material.AIR.toString(),
    var name: String = "",
    var lore: List<String> = listOf(),
    var amount: Int = 1,
    var enchantments: MutableMap<String, Int> = mutableMapOf(),
)