package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Punch : EnchantmentAPI("punch") {

    override val emoji = Emoji.BOW
    override val display = "衝擊"
    override val maxLevel = 2
    override val startLevel = 1
    override val description = listOf("")
    override val origin = Enchantment.ARROW_KNOCKBACK!!

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.BOW)
    }
}