package com.ec.manager.papi

import com.ec.manager.GlobalManager
import org.bukkit.entity.Player

abstract class PlaceholderAPI{
    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun isDynamic(): Boolean
    abstract fun placeholderKey(): String
    abstract fun onPlaceholderRequest(player: Player, text: String): String
}