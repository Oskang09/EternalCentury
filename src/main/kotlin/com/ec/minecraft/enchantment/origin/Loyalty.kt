package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Loyalty: EnchantmentAPI("loyalty") {

    override val emoji = Emoji.TRIDENT
    override val display = "忠誠"
    override val maxLevel = 3
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.LOYALTY!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.TRIDENT)
    }
}