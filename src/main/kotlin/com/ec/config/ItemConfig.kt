package com.ec.config

import com.ec.database.model.Item

data class ItemConfig(
    var id: String = "",
    val consume: Boolean = false,
    val commands: List<String> = listOf()
) : Item()