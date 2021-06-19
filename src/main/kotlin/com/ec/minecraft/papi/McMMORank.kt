package com.ec.minecraft.papi

import com.ec.extension.papi.PlaceholderAPI
import org.bukkit.entity.Player

class McMMORank: PlaceholderAPI() {

    override fun isDynamic(): Boolean {
        return false
    }

    override fun placeholderKey(): String {
        return "mcmmo_rank"
    }

    override fun onPlaceholderRequest(player: Player, text: String): String {
        return globalManager.mcmmo.getOverallRank(player).toString()
    }

}