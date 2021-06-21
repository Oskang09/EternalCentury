package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class QuickCharge: EnchantmentAPI("quick_charge") {

    override val emoji = Emoji.BOW
    override val display = "快速上弦"
    override val maxLevel = 3
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.QUICK_CHARGE!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.CROSSBOW)
    }
}