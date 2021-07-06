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
                "module": "activity",
                "activity": "zombie-fight",
            }
        */

        override fun action(player: Player) {
            when (type) {
                ModuleType.REWARD -> {
                    globalManager.players.getByPlayer(player).gameName = activity
                    globalManager.players.getByPlayer(player).gameState = if (activity == "") {
                        ECPlayerGameState.FREE
                    } else {
                        ECPlayerGameState.ACTIVITY
                    }
                    globalManager.activity.getActivityById(activity).onJoinActivity(player)
                }
                ModuleType.ITEM_PROVIDER -> {}
                else -> {}
            }
        }

        override fun check(player: Player): Boolean {
            return globalManager.activity.getActivityById(activity).running
        }

        override fun onFail(player: Player) {
            player.sendMessage(globalManager.message.system("活动还没开放，请查看活动时间。"))
        }

        override fun onSuccess(player: Player) {

        }

    }

}