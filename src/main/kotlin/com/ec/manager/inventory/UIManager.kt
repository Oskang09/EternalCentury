package com.ec.manager.inventory

import com.ec.manager.GlobalManager
import com.ec.manager.inventory.component.IteratorUI
import com.ec.manager.inventory.component.PaginationUI
import com.ec.minecraft.inventory.AuctionUI
import com.ec.minecraft.inventory.filter.ItemUI
import dev.reactant.reactant.core.component.Component
import org.bukkit.Material
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

    fun displayAdmin(player: Player) {
        uis["admin"]!!.displayTo(player)
    }

    fun displayItemFilter(player: Player, onSelect: (Material?, String?) -> Unit) {
        val ui = uis["item-filter"]!! as PaginationUI<ItemUI.InputProps>
        ui.displayWithProps(player, ItemUI.InputProps(onSelect = onSelect))
    }

    fun displayAuction(player: Player, props: AuctionUI.AuctionUIProps? = null) {
        val ui = uis["auction"]!! as IteratorUI<AuctionUI.AuctionUIProps>
        if (props == null) {
            ui.displayTo(player)
        } else {
            ui.displayWithProps(player, props)
        }
    }

    fun displayPlayer(player: Player) {
        uis["player"]!!.displayTo(player)
    }

    fun displayRepair(player: Player) {
        uis["repair"]!!.displayTo(player)
    }
}