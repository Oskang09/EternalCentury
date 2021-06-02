package com.ec.minecraft.enchantment.origin

import com.ec.config.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Respiration: EnchantmentAPI("respiration") {

    override fun getEmoji(): Emoji {
        return Emoji.SHIELD
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(
            Material.LEATHER_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.DIAMOND_HELMET,
            Material.IRON_HELMET,
            Material.GOLDEN_HELMET,
            Material.TURTLE_HELMET,
            Material.NETHERITE_HELMET,
        )
    }

    override fun getLore(): String {
        return "水中呼吸"
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
        return Enchantment.OXYGEN
    }
}