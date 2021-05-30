package com.ec.extension.inventory

import com.ec.minecraft.enchantment.Glow
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

@Component
class UIComponent {

    fun playerHead(player: Player, meta: ((SkullMeta) -> Unit)? = null): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        head.itemMeta<SkullMeta> {
            if (meta != null) {
                meta(this)
            }

            owningPlayer = Bukkit.getOfflinePlayer(player.uniqueId)
        }
        return head
    }

    fun arrowPrevious(page: Int): ItemStack {
        val arrow = ItemStack(Material.ARROW, page)
        arrow.itemMeta<ItemMeta> {
            setDisplayName("§b[§5系统§b] §6上一页")
        }
        return arrow
    }

    fun arrowNext(page: Int): ItemStack {
        val arrow = ItemStack(Material.ARROW, page)
        arrow.itemMeta<ItemMeta> {
            setDisplayName("§b[§5系统§b] §6下一页")
        }
        return arrow
    }

    fun woolAccept(): ItemStack {
        val wool = ItemStack(Material.GREEN_WOOL)
        wool.itemMeta<ItemMeta> {
            setDisplayName("§b[§5系统§b] §a确认")
        }
        return wool
    }

    fun woolDecline(): ItemStack {
        val wool = ItemStack(Material.RED_WOOL)
        wool.itemMeta<ItemMeta> {
            setDisplayName("§b[§5系统§b] §c取消")
        }
        return wool
    }

    fun withGlow(stack: ItemStack, meta: ((ItemMeta) -> Unit)? = null): ItemStack {
        stack.itemMeta<ItemMeta> {
            if (meta != null) {
                meta(this)
            }

            addEnchant(Glow(), 1 , true)
        }
        return stack
    }

}