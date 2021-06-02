package com.ec.extension.enchantment

import com.ec.extension.GlobalManager
import com.ec.config.model.Emoji
import com.ec.util.RomanUtil.toRoman
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget

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
    abstract fun isConflict(enchantment: EnchantmentAPI): Boolean

    open fun getOrigin(): Enchantment? {
        return null
    }

    private fun baseSupportedMaterial(): List<Material> {
        return listOf(Material.BOOK, Material.ENCHANTED_BOOK)
    }

    fun getDisplayLore(material: Material?, level: Int): String {
        var emojiString = ""
        if (material != null) {
            if (baseSupportedMaterial().contains(material) || isSupportedMaterial().contains(material)) {
                emojiString = "&e" +getEmoji().text + " "
            }
        }

        return (emojiString + "&7" + getLore() + " " + level.toRoman()).colorize()
    }

}
