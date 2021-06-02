package com.ec.extension.inventory

import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

@Component
class UIManager {

    private lateinit var globalManager: GlobalManager
    private val uis: MutableMap<String, UIProvider<*>> = HashMap()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopUI {
            uis[it.id] = it
            it.initialize(globalManager)
        }
    }

    fun displayTo(player: HumanEntity, name: String) {
        uis[name]?.displayTo(player)
    }

    fun displayRepair(player: Player) {
        uis["repair"]?.displayTo(player)
    }

    fun displayTitle(player: Player) {
        uis["title"]?.displayTo(player)
    }
}