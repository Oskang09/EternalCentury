package com.ec.extension.enchantment

import com.ec.extension.GlobalManager
import com.ec.util.RomanUtil.toRoman

abstract class EnchantmentAPI(val id: String) {

    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun getEmoji(): EnchantmentEmoji
    abstract fun getLore(): String
    abstract fun getMaxLevel(): Int
    abstract fun getStartLevel(): Int

    fun getDisplayLore(level: Int): String {
        return "§3" + getEmoji().text + " §7" + getLore() + " " + level.toRoman()
    }

}
