package com.ec.manager.wallet

import com.ec.database.Wallet
import com.ec.manager.GlobalManager
import org.bukkit.inventory.ItemStack

abstract class WalletAPI(val id: String) {
    protected lateinit var globalManager: GlobalManager

    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun getItemStack(wallet: Wallet): ItemStack
    abstract fun getGrade(wallet: Wallet): Int;
}