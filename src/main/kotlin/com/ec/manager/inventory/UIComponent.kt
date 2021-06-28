package com.ec.manager.inventory

import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

@Component
class UIComponent {

    fun item(material: Material, glowing: Boolean = false, meta: ((ItemMeta) -> Unit)? = null): ItemStack {
        val item = ItemStack(material)
        if (meta != null) {
            item.itemMeta<ItemMeta> {
                meta(this)
            }
        }

        if (glowing) {
            return withGlow(item)
        }
        return item
    }

    fun playerHead(textures: String, meta: ((SkullMeta) -> Unit)? = null): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        val hashAsId = UUID(textures.hashCode().toLong(), textures.hashCode().toLong())
        Bukkit.getUnsafe().modifyItemStack(head,
            "{SkullOwner:{Id:\"" + hashAsId.toString() + "\",Properties:{textures:[{Value:\""
                    + textures + "\"}]}}}"
        )

        head.itemMeta<SkullMeta> {
            if (meta != null) {
                meta(this)
            }
        }

        return head
    }

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

    fun arrowPrevious(): ItemStack {
        val arrow = ItemStack(Material.SOUL_CAMPFIRE)
        arrow.itemMeta<ItemMeta> {
            displayName("&b[&5系统&b] &6上一页".toComponent())
        }
        return arrow
    }

    fun arrowNext(): ItemStack {
        val arrow = ItemStack(Material.HOPPER)
        arrow.itemMeta<ItemMeta> {
            displayName("&b[&5系统&b] &6下一页".toComponent())
        }
        return arrow
    }

    fun woolAccept(): ItemStack {
        val wool = ItemStack(Material.GREEN_WOOL)
        wool.itemMeta<ItemMeta> {
            displayName("&b[&5系统&b] &a确认".toComponent())
        }
        return wool
    }

    fun woolDecline(): ItemStack {
        val wool = ItemStack(Material.RED_WOOL)
        wool.itemMeta<ItemMeta> {
            displayName("&b[&5系统&b] &c取消".toComponent())
        }
        return wool
    }

    fun withGlow(stack: ItemStack, meta: ((ItemMeta) -> Unit)? = null): ItemStack {
        stack.itemMeta<ItemMeta> {
            if (meta != null) {
                meta(this)
            }

            addEnchant(Enchantment.DURABILITY, 1 , true)
            addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
            )
        }
        return stack
    }
}