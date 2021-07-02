package com.ec.database.model

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class BattlePassReward(
    val display: ItemStack = ItemStack(Material.AIR),
    val description: List<String> = listOf(),
    val reward: Reward = Reward(),
)
