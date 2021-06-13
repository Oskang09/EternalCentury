package com.ec.extension.item

import com.ec.extension.GlobalManager
import me.oska.module.ItemProvider
import org.bukkit.inventory.ItemStack

class UguiProvider(private val globalManager: GlobalManager): ItemProvider() {

    override fun getAuthor(): String {
        return "Oska"
    }

    override fun getName(): String {
        return "EternalCentury"
    }

    override fun getVersion(): String {
        return "0.0.1"
    }

    override fun isSupported() {

    }

    override fun get(config: Map<*, *>): ItemStack {
        val id =config["id"] as String
        return globalManager.items.getItemByKey(id)
    }

    override fun key(): String {
        return "ec-item"
    }

}