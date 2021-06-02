package com.ec.minecraft.enchantment.origin

import com.ec.config.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Lure: EnchantmentAPI("bane_of_arthropods") {

    override fun getEmoji(): Emoji {
        return Emoji.FISHING_ROD
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.FISHING_ROD)
    }

    override fun getLore(): String {
        return "魚餌"
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
        return Enchantment.LURE
    }
}