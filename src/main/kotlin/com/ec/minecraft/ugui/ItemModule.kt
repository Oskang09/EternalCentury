package com.ec.minecraft.ugui

import com.ec.extension.GlobalManager
import com.ec.extension.ugui.ModuleAPI
import me.oska.module.Module
import me.oska.module.ModuleType
import org.bukkit.entity.Player

class ItemModule: ModuleAPI() {

    override fun getIdentifier(): String {
        return "ec-item"
    }

    override fun getName(): String {
        return "ItemModule"
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

        private val itemKey = config["item"] as String
        private val amount = config["amount"] as Int

        override fun action(player: Player) {
            when (type) {
                ModuleType.ITEM_PROVIDER -> {}
                ModuleType.REQUIREMENT -> {
                    globalManager.items.playerRemove(player, itemKey, amount)
                }
                ModuleType.REWARD -> {
                    player.inventory.addItem(globalManager.items.getItemByKey(itemKey))
                }
            }
        }

        override fun check(player: Player): Boolean {
            if (type == ModuleType.REQUIREMENT) {
                return globalManager.items.playerHas(player, itemKey, amount)
            }
            return true
        }

    }
}