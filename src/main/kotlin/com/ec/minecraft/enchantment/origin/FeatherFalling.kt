package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class FeatherFalling: EnchantmentAPI("feather_falling") {

    override val emoji = Emoji.SHIELD
    override val display = "輕盈"
    override val maxLevel = 2
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.PROTECTION_FALL!!

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