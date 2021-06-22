package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.manager.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class DepthStrider: EnchantmentAPI("depth_strider") {

    override val emoji = Emoji.SHIELD
    override val display = "深海漫遊"
    override val maxLevel = 3
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.DEPTH_STRIDER!!

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