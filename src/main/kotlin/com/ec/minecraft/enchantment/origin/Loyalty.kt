package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Loyalty: EnchantmentAPI("loyalty") {

    override fun getEmoji(): Emoji {
        return Emoji.TRIDENT
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.TRIDENT)
    }

    override fun getLore(): String {
        return "忠誠"
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.LOYALTY
    }
}