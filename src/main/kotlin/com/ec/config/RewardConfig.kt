package com.ec.config

data class RewardConfig(
    // ITEM, ENCHANTMENT, COMMAND
    val type: String,
    val display: String,
    val item: ItemConfig? = null,
    val itemId: String? = null,
    val enchantments: MutableMap<String, Int>? = null,
    val commands: MutableList<String>? = null
)