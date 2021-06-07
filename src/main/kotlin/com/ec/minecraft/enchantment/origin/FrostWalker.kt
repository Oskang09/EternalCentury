package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class FrostWalker: EnchantmentAPI("frost_walker") {

    override fun getEmoji(): Emoji {
        return Emoji.SNOWMAN
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(
            Material.LEATHER_BOOTS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_BOOTS,
            Material.DIAMOND_BOOTS,
            Material.GOLDEN_BOOTS,
            Material.NETHERITE_BOOTS,
        )
    }

    override fun getLore(): String {
        return "魚叉"
    }

    override fun getMaxLevel(): Int {
        return 2
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.FROST_WALKER
    }
}