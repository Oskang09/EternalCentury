package com.ec.minecraft.enchantment

import com.ec.extension.GlobalManager
import com.ec.extension.enchantment.EnchantmentAPI
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack

class Glow : EnchantmentAPI("GLOW") {

    override fun initialize(globalManager: GlobalManager) {

    }

    override fun getName(): String {
        return ""
    }

    override fun getMaxLevel(): Int {
        return 0
    }

    override fun getStartLevel(): Int {
        return 0
    }

    override fun getItemTarget(): EnchantmentTarget {
        return EnchantmentTarget.VANISHABLE
    }

    override fun isTreasure(): Boolean {
        return false
    }

    override fun isCursed(): Boolean {
        return false
    }

    override fun conflictsWith(other: Enchantment): Boolean {
        return false
    }

    override fun canEnchantItem(item: ItemStack): Boolean {
        return true
    }
}