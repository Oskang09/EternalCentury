package com.ec.minecraft.enchantment.origin

import com.ec.config.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Knockback: EnchantmentAPI("knockback") {

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
        return "擊退"
    }

    override fun getMaxLevel(): Int {
        return 2
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun isConflict(enchantment: EnchantmentAPI): Boolean {
        return false
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.KNOCKBACK
    }
}