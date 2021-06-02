package com.ec.minecraft.enchantment.origin

import com.ec.config.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Impaling: EnchantmentAPI("impaling") {

    override fun getEmoji(): Emoji {
        return Emoji.TRIDENT
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.TRIDENT)
    }

    override fun getLore(): String {
        return "深海漫遊"
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
        return Enchantment.CHANNELING
    }
}