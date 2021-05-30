package com.ec.minecraft.inventory

import com.ec.extension.GlobalManager
import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.UIProvider
import com.ec.util.roundTo
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.content.InventoryContents
import org.bukkit.inventory.meta.Damageable
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta

class RepairUI: UIProvider("repair") {

    override fun info(player: Player): UIBase {
        var repairRequired = 0.0
        val mainHand = player.inventory.itemInMainHand
        if (mainHand.hasItemMeta() && mainHand.itemMeta is Damageable) {
            val meta = mainHand.itemMeta as Damageable

            val currentDamage = meta.damage
            val maxDurability = mainHand.type.maxDurability
            val repairDamage = maxDurability - currentDamage

            repairRequired = (globalManager.serverConfig.repairPrice * repairDamage).toDouble()
        }

        return UIBase(
            title = "§b[§5系统§b] §f修理花费 §e- §6" + (repairRequired.roundTo(2)),
            rows = 1
        )
    }

    override fun init(player: Player, contents: InventoryContents) {
        val mainHand = player.inventory.itemInMainHand
        val meta = mainHand.itemMeta as Damageable
        val currentDurability = meta.damage
        val maxDurability = mainHand.type.maxDurability
        val repairDamage = maxDurability - currentDurability
        val repairRequired = (globalManager.serverConfig.repairPrice * repairDamage).toDouble()

        contents.set(0, 4, ClickableItem.empty(mainHand))
        contents.set(0, 6, ClickableItem.of(globalManager.component.woolDecline()) {
            player.closeInventory()
        })
        contents.set(0, 2, ClickableItem.of(globalManager.component.woolAccept()) {
            player.closeInventory()

//            globalManager.economy.withdrawPlayer(player, repairRequired)
            meta.damage = 0
            mainHand.itemMeta = (meta as ItemMeta)
        })
    }

    override fun update(player: Player, contents: InventoryContents) {

    }

}