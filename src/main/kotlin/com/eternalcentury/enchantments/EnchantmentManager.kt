package com.eternalcentury.enchantments

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner


@Component
class EnchantmentManager: LifeCycleHook {
    private val enchantments: MutableMap<NamespacedKey, Enchantment> = HashMap();

    override fun onEnable() {
        val field = Enchantment::class.java.getDeclaredField("acceptingNew")
        field.isAccessible = true
        field.set(null, true)

        Reflections("com.package.minecraft.enchantments", SubTypesScanner()).
        getSubTypesOf(Enchantment::class.java).
        forEach {
            val enchantment = it.newInstance()
            Enchantment.registerEnchantment(enchantment)

            enchantments[enchantment.key] = enchantment
        }
    }

}
