package com.eternalcentury.minecraft.enchantments

import com.eternalcentury.ReactantPlugin
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack

class ExampleEnchantment : Enchantment(NamespacedKey(ReactantPlugin.instance, "ExampleEnchantment")) {
    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getMaxLevel(): Int {
        TODO("Not yet implemented")
    }

    override fun getStartLevel(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemTarget(): EnchantmentTarget {
        TODO("Not yet implemented")
    }

    override fun isTreasure(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCursed(): Boolean {
        TODO("Not yet implemented")
    }

    override fun conflictsWith(other: Enchantment): Boolean {
        TODO("Not yet implemented")
    }

    override fun canEnchantItem(item: ItemStack): Boolean {
        TODO("Not yet implemented")
    }
}
