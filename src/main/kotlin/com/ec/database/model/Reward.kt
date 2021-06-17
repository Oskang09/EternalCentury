package com.ec.database.model

import com.ec.config.ItemConfig

open class Reward(
    // item, enchantment, command
    val type: String = "",
    val item: ItemConfig? = null,
    val itemId: String? = null,
    val enchantments: Map<String, Int>? = null,
    val commands: List<String>? = null
)