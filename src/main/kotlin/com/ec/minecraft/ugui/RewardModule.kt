package com.ec.minecraft.ugui

import com.ec.database.model.Reward
import com.ec.manager.GlobalManager
import com.ec.manager.ugui.ModuleAPI
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.oska.module.Module
import me.oska.module.ModuleType
import org.bukkit.entity.Player

class RewardModule: ModuleAPI() {

    private val mapper = jacksonObjectMapper()

    override fun getIdentifier(): String {
        return "reward"
    }

    override fun getName(): String {
        return "RewardModule"
    }

    override fun supportParallel(): Boolean {
        return true
    }

    override fun getModule(type: ModuleType, config: Map<*, *>): Module {
        val json = mapper.writeValueAsString(config["reward"] as Map<*, *>)
        return ActionModule(type, globalManager, mapper.readValue(json, Reward::class.java))
    }

    internal class ActionModule(
        val type: ModuleType,
        val globalManager: GlobalManager,
        val config: Reward,
    ): Module() {

        /*
            {
                "module": "reward",
                "reward": {
                    "type": "item" | "enchantment" | "command",
                    # choose between `item` and `itemId`
                    "item" : {
                        "material": "",
                        "name": "",
                        "lore": [],
                        "amount": 1,
                        "enchantments": {}
                    },
                    "itemId": "",
                    "enchantments": {},
                    "commands": []
                }
            }
        */

        override fun action(player: Player) {
            when (type) {
                ModuleType.ITEM_PROVIDER -> {}
                ModuleType.REQUIREMENT -> {}
                ModuleType.REWARD -> {
                    globalManager.sendRewardToPlayer(player, config)
                }
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