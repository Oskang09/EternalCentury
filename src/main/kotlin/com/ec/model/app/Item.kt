package com.ec.model.app

import org.bukkit.Material

open class Item(
    var material: String = Material.AIR.toString(),
    var name: String = "",
    var lore: List<String> = listOf(),
    var amount: Int = 1,
    var enchantments: MutableMap<String, Int> = mutableMapOf(),
)