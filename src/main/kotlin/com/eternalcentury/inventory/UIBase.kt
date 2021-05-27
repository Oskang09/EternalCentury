package com.eternalcentury.inventory

import org.bukkit.event.inventory.InventoryType

data class UIBase(
    val id: String = "ui-default",
    val rows: Int = 6,
    val cols: Int = 9,
    val title: String = "",
    val type: InventoryType = InventoryType.CHEST,
    val closable: Boolean = true
)