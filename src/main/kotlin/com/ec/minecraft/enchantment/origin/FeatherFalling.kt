package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class FeatherFalling: EnchantmentAPI("feather_falling") {

    override fun getEmoji(): Emoji {
        return Emoji.SHIELD
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
        return "輕盈"
    }

    override fun getMaxLevel(): Int {
        return 2
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.PROTECTION_FALL
    }
}