package com.eternalcentury.inventory

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

@Component
class UIComponent {

    fun playerHead(player: Player, meta: (SkullMeta) -> Unit): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        head.itemMeta<SkullMeta> {
            meta
            owningPlayer = Bukkit.getOfflinePlayer(player.uniqueId)
        }
        return head
    }

}