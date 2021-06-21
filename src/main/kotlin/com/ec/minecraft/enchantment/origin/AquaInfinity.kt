package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class AquaInfinity: EnchantmentAPI("aqua_infinity") {

    override val emoji = Emoji.TOOLS
    override val display = "親水性"
    override val maxLevel = 1
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.WATER_WORKER!!

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

}