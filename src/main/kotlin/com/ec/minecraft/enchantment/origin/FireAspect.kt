package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class FireAspect: EnchantmentAPI("fire_aspect") {

    override val emoji = Emoji.FIRE
    override val display = "燃燒"
    override val maxLevel = 2
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.FIRE_ASPECT!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(
            Material.NETHERITE_SWORD,
            Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.STONE_SWORD,
            Material.WOODEN_SWORD,
        )
    }
}