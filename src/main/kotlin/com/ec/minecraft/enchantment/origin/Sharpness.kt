package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Sharpness : EnchantmentAPI("sharpness") {

    override fun getEmoji(): Emoji {
        return Emoji.SWORD
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(
            Material.NETHERITE_SWORD,
            Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.STONE_SWORD,
            Material.WOODEN_SWORD,
            Material.NETHERITE_AXE,
            Material.DIAMOND_AXE,
            Material.GOLDEN_AXE,
            Material.IRON_AXE,
            Material.STONE_AXE,
            Material.WOODEN_AXE,
        )
    }

    override fun getLore(): String {
        return "鋒利"
    }

    override fun getMaxLevel(): Int {
        return 5
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.DAMAGE_ALL
    }

}