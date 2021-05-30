package com.ec.minecraft.papi

import com.ec.extension.GlobalManager
import com.ec.extension.papi.PlaceholderAPI
import org.bukkit.entity.Player

class ExamplePAPI: PlaceholderAPI() {

    override fun initialize(globalManager: GlobalManager) {

    }

    override fun placeholderKey(): String {
        return ""
    }

    override fun onPlaceholderRequest(player: Player): String {
        return ""
    }
}