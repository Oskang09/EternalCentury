package com.ec.minecraft.enchantment.origin

import com.ec.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Power: EnchantmentAPI("power") {

    override fun getEmoji(): Emoji {
        return Emoji.BOW
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.BOW)
    }

    override fun getLore(): String {
        return "强力"
    }

    override fun getMaxLevel(): Int {
        return 5
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.ARROW_DAMAGE
    }
}