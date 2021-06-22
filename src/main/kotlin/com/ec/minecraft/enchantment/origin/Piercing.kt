package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.manager.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Piercing: EnchantmentAPI("piercing") {

    override val emoji = Emoji.BOW
    override val display = "分裂箭矢"
    override val maxLevel = 4
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.PIERCING!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.CROSSBOW)
    }
}