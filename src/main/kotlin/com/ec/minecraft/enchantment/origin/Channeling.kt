package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Channeling: EnchantmentAPI("channeling") {

    override fun getEmoji(): Emoji {
        return Emoji.TRIDENT
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.TRIDENT)
    }

    override fun getLore(): String {
        return "喚雷"
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.CHANNELING
    }
}