package com.ec.extension.enchantment

import com.ec.ECCore
import com.ec.extension.GlobalManager
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

abstract class EnchantmentAPI(private val enchantmentKey: String) : Enchantment(NamespacedKey(ECCore.instance, enchantmentKey)) {

    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    fun getEnchantmentKey(): NamespacedKey {
        return NamespacedKey(ECCore.instance, enchantmentKey)
    }
}
