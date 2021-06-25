package com.ec.minecraft.papi

import com.ec.manager.papi.PlaceholderAPI
import com.ec.util.InstantUtil.toMalaysiaReadableTime
import org.bukkit.entity.Player

class ECCooldownDisplay: PlaceholderAPI() {

    override fun isDynamic(): Boolean {
        return true
    }

    override fun placeholderKey(): String {
        return "cooldown_display_"
    }

    override fun onPlaceholderRequest(player: Player, text: String): String {
        val state = globalManager.states.getPlayerState(player)
        val doneAt = state.cooldown[text] ?: return "您还没完成过这个任务"
        return doneAt.toMalaysiaReadableTime()
    }

}