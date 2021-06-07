package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Mending: EnchantmentAPI("mending") {

    override fun getEmoji(): Emoji {
        return Emoji.TOOLS
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(
            Material.ELYTRA,
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD,
            Material.NETHERITE_SWORD,
            Material.WOODEN_SHOVEL,
            Material.STONE_SHOVEL,
            Material.IRON_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.NETHERITE_SHOVEL,
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.NETHERITE_PICKAXE,
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.DIAMOND_AXE,
            Material.GOLDEN_AXE,
            Material.NETHERITE_AXE,
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.DIAMOND_HOE,
            Material.GOLDEN_HOE,
            Material.NETHERITE_HOE,
            Material.LEATHER_BOOTS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_BOOTS,
            Material.DIAMOND_BOOTS,
            Material.GOLDEN_BOOTS,
            Material.NETHERITE_BOOTS,
            Material.LEATHER_LEGGINGS,
            Material.CHAINMAIL_LEGGINGS,
            Material.IRON_LEGGINGS,
            Material.DIAMOND_LEGGINGS,
            Material.GOLDEN_LEGGINGS,
            Material.NETHERITE_LEGGINGS,
            Material.LEATHER_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE,
            Material.IRON_CHESTPLATE,
            Material.DIAMOND_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE,
            Material.NETHERITE_CHESTPLATE,
            Material.LEATHER_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.DIAMOND_HELMET,
            Material.IRON_HELMET,
            Material.GOLDEN_HELMET,
            Material.TURTLE_HELMET,
            Material.NETHERITE_HELMET,
        )
    }

    override fun getLore(): String {
        return "修補"
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.MENDING
    }
}