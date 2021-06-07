package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class AquaInfinity: EnchantmentAPI("aqua_infinity") {

    override fun getEmoji(): Emoji {
        return Emoji.TOOLS
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
        return "親水性"
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.WATER_WORKER
    }

}