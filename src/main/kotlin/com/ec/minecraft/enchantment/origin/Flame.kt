package com.ec.minecraft.enchantment.origin

import com.ec.config.model.Emoji
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Flame: EnchantmentAPI("flame") {

    override fun getEmoji(): Emoji {
        return Emoji.BOW
    }

    override fun isSupportedMaterial(): List<Material> {
        return listOf(Material.BOW)
    }

    override fun getLore(): String {
        return "火焰"
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun isConflict(enchantment: EnchantmentAPI): Boolean {
        return false
    }

    override fun getOrigin(): Enchantment {
        return Enchantment.ARROW_FIRE
    }

}