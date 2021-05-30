package com.ec.extension.title

import com.ec.extension.GlobalManager
import com.ec.extension.player.ECPlayer
import org.bukkit.inventory.ItemStack

abstract class TitleAPI(val id: String, val position: Int) {
    protected lateinit var globalManager: GlobalManager

    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun uiDisplay(stack: ItemStack): ItemStack;
    abstract fun display(): String;
    abstract fun unlockCondition(ecPlayer: ECPlayer): Boolean;
}
