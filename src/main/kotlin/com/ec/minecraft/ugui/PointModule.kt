package com.ec.minecraft.ugui

import com.ec.extension.GlobalManager
import com.ec.extension.ugui.ModuleAPI
import me.oska.module.Module
import me.oska.module.ModuleNotConfigured
import me.oska.module.ModuleType
import org.bukkit.entity.Player

class PointModule: ModuleAPI() {

    override fun getIdentifier(): String {
        return "point"
    }

    override fun getName(): String {
        return "PointModule"
    }

    override fun supportParallel(): Boolean {
        return true
    }

    override fun getModule(type: ModuleType, config: Map<*, *>): Module {
        val name = config["name"] ?: ModuleNotConfigured("'name' is not defined.")
        val value = config["value"] ?: ModuleNotConfigured("'value' is not defined.")
        return ActionModule(
            type,
            globalManager,
            name as String, value as Double
        )
    }

    internal class ActionModule(
        val type: ModuleType,
        val globalManager: GlobalManager,
        val name: String, val value: Double
    ): Module() {

        override fun action(player: Player) {
            when (type) {
                ModuleType.REQUIREMENT -> globalManager.points.withdrawPlayerPoint(player, name, value)
                ModuleType.REWARD -> globalManager.points.depositPlayerPoint(player, name, value)
                ModuleType.ITEM_PROVIDER -> {}
            }
        }

        override fun check(player: Player): Boolean {
            if (type == ModuleType.REQUIREMENT) {
                return globalManager.points.hasPlayerPoint(player, name, value)
            }
            return true
        }

    }

}