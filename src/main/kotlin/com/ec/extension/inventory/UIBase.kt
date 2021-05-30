package com.ec.extension.inventory

import org.bukkit.event.inventory.InventoryType

data class UIBase(
    val rows: Int = 6,
    val cols: Int = 9,
    val title: String = "",
    val type: InventoryType = InventoryType.CHEST,
    val closable: Boolean = true
)