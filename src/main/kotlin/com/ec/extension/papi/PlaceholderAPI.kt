package com.ec.extension.papi

import com.ec.extension.GlobalManager
import org.bukkit.entity.Player

abstract class PlaceholderAPI{
    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun placeholderKey(): String
    abstract fun onPlaceholderRequest(player: Player): String
}