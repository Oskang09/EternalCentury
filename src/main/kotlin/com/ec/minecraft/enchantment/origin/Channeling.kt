package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Channeling: EnchantmentAPI("channeling") {

    override val emoji = Emoji.TRIDENT
    override val display = "喚雷"
    override val maxLevel = 1
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.CHANNELING!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.TRIDENT)
    }
}