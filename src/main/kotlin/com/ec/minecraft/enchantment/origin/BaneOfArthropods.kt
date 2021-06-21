package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class BaneOfArthropods: EnchantmentAPI("bane_of_arthropods") {

    override val emoji = Emoji.SWORD
    override val display = "節肢剋星"
    override val maxLevel = 5
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.DAMAGE_ARTHROPODS!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(
            Material.NETHERITE_SWORD,
            Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.STONE_SWORD,
            Material.WOODEN_SWORD,
            Material.NETHERITE_AXE,
            Material.DIAMOND_AXE,
            Material.GOLDEN_AXE,
            Material.IRON_AXE,
            Material.STONE_AXE,
            Material.WOODEN_AXE,
        )
    }
}