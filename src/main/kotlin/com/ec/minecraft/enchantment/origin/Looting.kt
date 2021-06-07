package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Looting: EnchantmentAPI("looting") {

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
        )
    }

    override fun getLore(): String {
        return "掠奪"
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.LOOT_BONUS_MOBS
    }
}