package com.ec.config

import org.bukkit.inventory.ItemStack

data class StoreConfig(
    val display: String,
    val purchasing: List<StoreConfigItem>,
    val selling: List<StoreConfigItem>
)

data class StoreConfigItem(
    val stack: Map<String, Any>,
    val amount: Int,
    val price: Double
) {
    val item = ItemStack.deserialize(stack)
    val totalPrice = price * amount

    fun isSimilar(target: ItemStack) : Boolean{
        return item.isSimilar(target)
    }
}