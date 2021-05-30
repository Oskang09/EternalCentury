package com.ec.extension.inventory

import com.ec.extension.GlobalManager
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryProvider
import org.bukkit.entity.Player

abstract class UIProvider(val id: String): InventoryProvider {

    protected abstract fun info(player: Player): UIBase

    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    fun displayTo(player: Player, page: Int = 1) {
        val base = info(player);

        SmartInventory.builder()
            .id(id)
            .provider(this)
            .type(base.type)
            .size(base.rows, base.cols)
            .closeable(base.closable)
            .title(base.title)
            .build()
            .open(player, page)
    }

}
