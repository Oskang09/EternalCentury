package com.ec.manager.enchantment

import com.ec.model.Emoji
import com.ec.manager.GlobalManager
import com.ec.util.RomanUtil.toRoman
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

abstract class EnchantmentAPI(val id: String) {

    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract val description: List<String>
    abstract val emoji: Emoji
    abstract val display: String
    abstract val maxLevel: Int
    abstract val startLevel: Int
    open val origin: Enchantment? = null
    abstract fun isSupportedMaterial(): List<Material>


    private fun baseSupportedMaterial(): List<Material> {
        return listOf(Material.BOOK, Material.ENCHANTED_BOOK)
    }

    fun isSupported(material: Material): Boolean {
        return baseSupportedMaterial().contains(material) || isSupportedMaterial().contains(material)
    }

    fun getDisplayLore(level: Int): String {
        return ("&e" +emoji.text + " &7" + display + " " + level.toRoman()).colorize()
    }

}
