package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.manager.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Respiration: EnchantmentAPI("respiration") {

    override val emoji = Emoji.SHIELD
    override val display = "水中呼吸"
    override val maxLevel = 3
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.OXYGEN!!

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