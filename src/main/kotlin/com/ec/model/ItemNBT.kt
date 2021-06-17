package com.ec.model

data class ItemNBT(
    var id: String = "DEFAULT",
    val enchantments: MutableMap<String, Int> = mutableMapOf()
)