package com.ec.minecraft.papi

import com.ec.manager.papi.PlaceholderAPI
import org.bukkit.entity.Player

class ECPlayerTitle: PlaceholderAPI() {
    override fun isDynamic(): Boolean {
        return false
    }

    override fun placeholderKey(): String {
        return "player_title"
    }

    override fun onPlaceholderRequest(player: Player, text: String): String {
        return "&e&lsome debug message"
    }
}