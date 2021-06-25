package com.ec.minecraft.ugui

import com.ec.manager.GlobalManager
import com.ec.manager.ugui.ModuleAPI
import me.oska.module.Module
import me.oska.module.ModuleType
import org.bukkit.entity.Player

class PartyModule: ModuleAPI() {

    override fun getIdentifier(): String {
        return "ec-party"
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

        private val numOfMembers = config["num_of_member"] as Int?
        private val challenge = config["challenge"] as String
        private val actionType  = config["type"] as String
        private val teleportId = config["teleport_id"] as String

        override fun action(player: Player) {
            when (type) {
                ModuleType.REQUIREMENT -> {}
                ModuleType.ITEM_PROVIDER -> {}
                ModuleType.REWARD -> {
                    when (actionType) {
                        "TELEPORT" -> globalManager.mcmmo.partyTeleport(player,challenge, teleportId)
                    }
                }
            }
        }

        override fun check(player: Player): Boolean {
            if (type == ModuleType.REQUIREMENT) {
                if (numOfMembers != null) {
                    val partyMembers = globalManager.mcmmo.getPlayerParty(player)
                    if (partyMembers.size != numOfMembers) {
                        return false
                    }
                }
                return globalManager.mcmmo.partyIsNearby(player, challenge)
            }
            return true
        }

        override fun onFail(player: Player) {

        }

        override fun onSuccess(player: Player) {

        }

    }

}