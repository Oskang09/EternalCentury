package com.ec.minecraft.enchantment

import com.ec.extension.enchantment.EnchantmentAPI
import com.ec.extension.enchantment.EnchantmentEmoji

class Sharpness : EnchantmentAPI("DEBUG") {

    override fun getEmoji(): EnchantmentEmoji {
        return EnchantmentEmoji.SWORD
    }

    override fun getLore(): String {
        return "锋利"
    }

    override fun getMaxLevel(): Int {
        return 5
    }

    override fun getStartLevel(): Int {
        return 1
    }
}