package com.ec.minecraft.enchantment.origin

import com.ec.config.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Fortune: EnchantmentAPI("fortune") {

    override fun getEmoji(): Emoji {
        return Emoji.TOOLS
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(
            Material.SHEARS,
            Material.NETHERITE_AXE,
            Material.DIAMOND_AXE,
            Material.GOLDEN_AXE,
            Material.IRON_AXE,
            Material.STONE_AXE,
            Material.WOODEN_AXE,
            Material.NETHERITE_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.IRON_PICKAXE,
            Material.STONE_PICKAXE,
            Material.WOODEN_PICKAXE,
            Material.NETHERITE_HOE,
            Material.DIAMOND_HOE,
            Material.GOLDEN_HOE,
            Material.IRON_HOE,
            Material.STONE_HOE,
            Material.WOODEN_HOE,
            Material.NETHERITE_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.IRON_SHOVEL,
            Material.STONE_SHOVEL,
            Material.WOODEN_SHOVEL,
        )
    }

    override fun getLore(): String {
        return "幸運"
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun isConflict(enchantment: EnchantmentAPI): Boolean {
        return false
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.LOOT_BONUS_BLOCKS
    }

}