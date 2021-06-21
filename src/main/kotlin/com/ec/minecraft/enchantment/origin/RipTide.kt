package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class RipTide: EnchantmentAPI("rip_tide") {

    override val emoji = Emoji.TRIDENT
    override val display = "波濤"
    override val maxLevel = 3
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.RIPTIDE!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.TRIDENT)
    }
}