package com.ec.extension.enchantment

import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

@Component
class EnchantmentManager {
    private val enchantments: MutableMap<NamespacedKey, EnchantmentAPI> = HashMap();
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        val field = Enchantment::class.java.getDeclaredField("acceptingNew")
        field.isAccessible = true
        field.set(null, true)

        globalManager.reflections.loopEnchantments {
            Enchantment.registerEnchantment(it)
            it.initialize(globalManager)
            enchantments[it.key] = it
        }
    }

}
