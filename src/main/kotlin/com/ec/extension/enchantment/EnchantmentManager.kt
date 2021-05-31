package com.ec.extension.enchantment

import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

@Component
class EnchantmentManager {
    private val enchantments: MutableMap<String, EnchantmentAPI> = HashMap();
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopEnchantments {
            it.initialize(globalManager)
            enchantments[it.id] = it
        }
    }

    fun getEnchantments(): MutableCollection<EnchantmentAPI> {
        return enchantments.values
    }

}
