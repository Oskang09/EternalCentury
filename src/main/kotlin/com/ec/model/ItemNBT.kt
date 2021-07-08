package com.ec.model

data class ItemNBT(
    var id: String = "",
    val enchantments: MutableMap<String, Int> = mutableMapOf(),
    val skills: MutableMap<String, Int> = mutableMapOf(),
)