package com.ec.model

data class ItemNBT(
    var id: String = "",
    val enchantments: MutableMap<String, Int> = mutableMapOf()
)