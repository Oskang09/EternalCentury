package com.ec.minecraft.trait

import com.ec.extension.trait.TraitAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.event.EventHandler
import org.bukkit.inventory.meta.Damageable

class RepairTrait: TraitAPI("repair") {

    @EventHandler
    fun click(event: NPCRightClickEvent) {
        if (event.npc.id == npc.id) {
            val player = event.clicker
            val item = event.clicker.inventory.itemInMainHand
            if (item.hasItemMeta() && item.itemMeta is Damageable) {
                return globalManager.inventory.displayRepair(player);
            }

            globalManager.inventory.displayTo(player, "admin")
//            globalManager.inventory.displayTitle(player)
            player.sendMessage(globalManager.message.npc(npc, "您的物品暂时不需要修理"))
        }
    }

}