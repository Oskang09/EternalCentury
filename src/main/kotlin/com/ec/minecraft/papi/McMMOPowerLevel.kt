package com.ec.minecraft.papi

import com.ec.manager.papi.PlaceholderAPI
import org.bukkit.entity.Player

class McMMOPowerLevel: PlaceholderAPI() {

    override fun isDynamic(): Boolean {
        return false
    }

    override fun placeholderKey(): String {
        return "mcmmo_power_level"
    }

    override fun onPlaceholderRequest(player: Player, text: String): String {
        val mcmmoPlayer = globalManager.mcmmo.getPlayer(player)
        if (mcmmoPlayer != null) {
            return mcmmoPlayer.powerLevel.toString()
        }
        return "0"
    }

}