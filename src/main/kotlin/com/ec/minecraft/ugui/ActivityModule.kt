package com.ec.minecraft.ugui

import com.ec.manager.GlobalManager
import com.ec.manager.ugui.ModuleAPI
import com.ec.model.player.ECPlayerGameState
import me.oska.module.Module
import me.oska.module.ModuleNotConfigured
import me.oska.module.ModuleType
import org.bukkit.entity.Player

class ActivityModule: ModuleAPI() {

    override fun getIdentifier(): String {
        return "activity"
    }

    override fun getName(): String {
        return "ActivityModule"
    }

    override fun supportParallel(): Boolean {
        return true
    }

    override fun getModule(type: ModuleType, config: Map<*, *>): Module {
        val activity = config["activity"] as String
        return ActionModule(
            type,
            globalManager,
            activity
        )
    }

    internal class ActionModule(
        val type: ModuleType,
        val globalManager: GlobalManager,
        val activity: String,
    ): Module() {

        /*
            {
                "module": "state",
                "activity": "zombie-fight"
            }
        */

        override fun action(player: Player) {
            when (type) {
                ModuleType.REQUIREMENT -> {}
                ModuleType.REWARD -> {
                    globalManager.players.getByPlayer(player).activityType = activity
                    globalManager.players.getByPlayer(player).gameState = if (activity == "") {
                        ECPlayerGameState.FREE
                    } else {
                        ECPlayerGameState.ACTIVITY
                    }
                }
                ModuleType.ITEM_PROVIDER -> {}
            }
        }

        override fun check(player: Player): Boolean {
            return true
        }

        override fun onFail(player: Player) {

        }

        override fun onSuccess(player: Player) {

        }

    }

}