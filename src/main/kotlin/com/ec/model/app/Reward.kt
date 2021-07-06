package com.ec.model.app

open class Reward(
    // item, enchantment, command
    val type: String = "",
    val item: Item? = null,
    val itemId: String? = null,
    val enchantments: Map<String, Int>? = null,
    val commands: List<String>? = null
)