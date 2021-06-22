package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.manager.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class LuckOfTheSea: EnchantmentAPI("luck_of_the_sea") {

    override val emoji = Emoji.FISHING_ROD
    override val display = "海洋祝福"
    override val maxLevel = 3
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.LUCK!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.FISHING_ROD)
    }
}