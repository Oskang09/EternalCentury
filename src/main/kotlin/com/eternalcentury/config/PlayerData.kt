package com.eternalcentury.config

import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

data class PlayerData(
    var uuid: UUID = UUID(0L, 0L),
    var numOfStorage: Int = 1,
    var storage: MutableMap<Number, MutableList<ItemStack>> = HashMap()
)