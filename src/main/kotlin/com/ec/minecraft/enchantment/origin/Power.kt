package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.manager.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Power: EnchantmentAPI("power") {

    override val emoji = Emoji.BOW
    override val display = "强力"
    override val maxLevel = 5
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.ARROW_DAMAGE!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.BOW)
    }
}