package com.ec.minecraft.ugui

import com.ec.extension.GlobalManager
import com.ec.extension.ugui.ModuleAPI
import me.oska.module.Module
import me.oska.module.ModuleType
import org.bukkit.entity.Player

class PartyModule: ModuleAPI() {

    override fun getIdentifier(): String {
        return "party"
    }

    override fun getName(): String {
        return "PartyModule"
    }

    override fun supportParallel(): Boolean {
        return true
    }

    override fun getModule(type: ModuleType, config: Map<*, *>): Module {
        return ActionModule(type, globalManager, config)
    }

    internal class ActionModule(
        val type: ModuleType,
        val globalManager: GlobalManager,
        val config: Map<*, *>,
    ): Module() {

        private val challenge = config["challenge"] as String
        private val actionType  = config["type"] as String
        private val teleportId = config["teleport_id"] as String

        override fun action(player: Player) {
            when (type) {
                ModuleType.REQUIREMENT -> {}
                ModuleType.ITEM_PROVIDER -> {}
                ModuleType.REWARD -> {
                    when (actionType) {
                        "TELEPORT" -> globalManager.mcmmoPartyTeleport(player,challenge, teleportId)
                    }
                }
            }
        }

        override fun check(player: Player): Boolean {
            if (type == ModuleType.REQUIREMENT) {
                return globalManager.mcmmoPartyIsNearby(player, challenge)
            }
            return true
        }

    }

}