package com.ec.config

import org.bukkit.Material

data class ItemData(
    var material: String = Material.AIR.toString(),
    var name: String = "",
    var lore: List<String> = listOf(),
    var amount: Int,
    var enchantments: MutableMap<String, Int> = mutableMapOf(),
);