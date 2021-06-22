package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.manager.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Looting: EnchantmentAPI("looting") {

    override val emoji = Emoji.SWORD
    override val display = "掠奪"
    override val maxLevel = 3
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.LOOT_BONUS_MOBS!!

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