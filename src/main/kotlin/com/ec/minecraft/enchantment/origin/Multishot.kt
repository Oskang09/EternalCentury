package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Multishot: EnchantmentAPI("multishot") {

    override val emoji = Emoji.BOW
    override val display = "分裂箭矢"
    override val maxLevel = 1
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.MULTISHOT!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.CROSSBOW)
    }
}