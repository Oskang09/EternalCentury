package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class FrostWalker: EnchantmentAPI("frost_walker") {

    override val emoji = Emoji.TOOLS
    override val display = "魚叉"
    override val maxLevel = 2
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.FROST_WALKER!!

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
}