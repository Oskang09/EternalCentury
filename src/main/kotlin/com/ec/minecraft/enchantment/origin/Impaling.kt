package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.manager.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Impaling: EnchantmentAPI("impaling") {

    override val emoji = Emoji.TRIDENT
    override val display = "鱼叉"
    override val maxLevel = 3
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.IMPALING!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.TRIDENT)
    }
}