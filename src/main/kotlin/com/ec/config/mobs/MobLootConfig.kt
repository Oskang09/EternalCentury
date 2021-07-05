package com.ec.config.mobs

import com.ec.database.model.Item

class MobLootConfig(
    val item: Item? = null,
    val itemId: String = "",
    val base: Int = 1,
)