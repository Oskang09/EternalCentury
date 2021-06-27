package com.ec.minecraft.ugui

import com.ec.manager.GlobalManager
import com.ec.manager.ugui.ModuleAPI
import me.oska.module.Module
import me.oska.module.ModuleType
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class WorldModule: ModuleAPI() {

    override fun getIdentifier(): String {
        return "world"
    }

    override fun getName(): String {
        return "WorldModule"
    }

    override fun supportParallel(): Boolean {
        return false
    }

    override fun getModule(type: ModuleType, config: Map<*, *>): Module {
        return ActionModule(type, config["world"] as String, globalManager)
    }

    internal class ActionModule(
        private val type: ModuleType,
        private val world: String,
        private val globalManager: GlobalManager,
    ) : Module() {

        /*
            {
                "module": "world",
                "world": "nether"
            }
        */

        override fun action(player: Player) {
        }

        override fun check(player: Player): Boolean {
            return Bukkit.getWorld(world) != null
        }

        override fun onFail(player: Player) {

        }

        override fun onSuccess(player: Player) {
        }

    }
}