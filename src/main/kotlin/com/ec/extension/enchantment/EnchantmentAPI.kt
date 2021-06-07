package com.ec.extension.enchantment

import com.ec.model.Emoji
import com.ec.extension.GlobalManager
import com.ec.util.RomanUtil.toRoman
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

abstract class EnchantmentAPI(val id: String) {

    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun getEmoji(): Emoji
    abstract fun isSupportedMaterial(): List<Material>
    abstract fun getLore(): String
    abstract fun getMaxLevel(): Int
    abstract fun getStartLevel(): Int

    open fun getOrigin(): Enchantment? {
        return null
    }

    private fun baseSupportedMaterial(): List<Material> {
        return listOf(Material.BOOK, Material.ENCHANTED_BOOK)
    }

    fun isSupported(material: Material): Boolean {
        return baseSupportedMaterial().contains(material) || isSupportedMaterial().contains(material)
    }

    fun getDisplayLore(level: Int): String {
        return ("&e" +getEmoji().text + " &7" + getLore() + " " + level.toRoman()).colorize()
    }

}
