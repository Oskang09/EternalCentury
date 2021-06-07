package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class FireAspect: EnchantmentAPI("fire_aspect") {

    override fun getEmoji(): Emoji {
        return Emoji.FIRE
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
        return "燃燒"
    }

    override fun getMaxLevel(): Int {
        return 2
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.FIRE_ASPECT
    }
}