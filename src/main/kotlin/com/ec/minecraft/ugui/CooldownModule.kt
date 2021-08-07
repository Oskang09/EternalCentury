package com.ec.minecraft.ugui

import com.ec.manager.GlobalManager
import com.ec.manager.ugui.ModuleAPI
import me.oska.module.Module
import me.oska.module.ModuleType
import org.bukkit.entity.Player
import java.time.Instant

class CooldownModule: ModuleAPI() {

    override fun getIdentifier(): String {
        return "cooldown"
    }

    override fun getName(): String {
        return "CooldownModule"
    }

    override fun supportParallel(): Boolean {
        return false
    }

    override fun getModule(type: ModuleType, config: Map<*, *>): Module {
        val id = config["id"] as String
        val second = config["second"] as Int
        return ActionModule(id, second, globalManager)
    }

    internal class ActionModule constructor(
        private val key: String,
        private val second: Int,
        private val globalManager: GlobalManager
    ): Module() {

        /*
            {
                "module": "cooldown",
                "id": "any unique / shared id",
                "second": 60,
            }
        */

        override fun action(player: Player) {
            globalManager.states.updateStateConfig(player) {
                it.cooldown[key] = Instant.now().epochSecond
            }
        }

        override fun check(player: Player): Boolean {
            val state = globalManager.states.getStateConfig(player)
            val current = Instant.now().epochSecond
            val doneAt = state.cooldown[key] ?: return true
            return current - doneAt >= second
        }

        override fun onFail(player: Player) {
            player.sendMessage(globalManager.message.system("&f您的冷却时间还未到无法完成任务。"))
        }

        override fun onSuccess(player: Player) {

        }

    }
}