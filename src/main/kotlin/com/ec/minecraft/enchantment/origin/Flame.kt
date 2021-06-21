package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Flame: EnchantmentAPI("flame") {

    override val emoji = Emoji.BOW
    override val display = "火焰"
    override val maxLevel = 1
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.ARROW_FIRE!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.BOW)
    }

}