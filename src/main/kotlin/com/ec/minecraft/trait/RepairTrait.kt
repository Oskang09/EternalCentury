package com.ec.minecraft.trait

import com.ec.extension.trait.TraitAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.util.DataKey
import org.bukkit.event.EventHandler
import org.bukkit.inventory.meta.Damageable

class RepairTrait: TraitAPI("repair") {

    override fun load(key: DataKey) {

    }

    override fun save(key: DataKey) {

    }

    @EventHandler
    fun click(event: NPCRightClickEvent) {
        if (event.npc.id == npc.id) {
            val player = event.clicker
            val item = event.clicker.inventory.itemInMainHand
            if (item.hasItemMeta() && item.itemMeta is Damageable) {
                return globalManager.inventory.displayRepair(player);
            }

            player.sendMessage("Item no need repair")
        }
    }

}