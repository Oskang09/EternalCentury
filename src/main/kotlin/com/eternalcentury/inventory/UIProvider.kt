package com.eternalcentury.inventory

import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryProvider
import org.bukkit.entity.Player

abstract class UIProvider: InventoryProvider {
    protected abstract fun info(player: Player): UIBase

    fun displayTo(player: Player) {
        val base = info(player);

        SmartInventory.builder()
            .id(base.id)
            .provider(this)
            .type(base.type)
            .size(base.rows, base.cols)
            .closeable(base.closable)
            .title(base.title)
            .build()
            .open(player)
    }

}
