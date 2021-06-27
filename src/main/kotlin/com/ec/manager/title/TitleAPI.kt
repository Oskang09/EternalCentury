package com.ec.manager.title

import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class TitleAPI(val id: String, val position: Int) {
    protected lateinit var globalManager: GlobalManager

    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun getItemStack(stack: ItemStack): ItemStack;
    abstract fun getDisplay(): String;
    abstract fun unlockCondition(ecPlayer: ECPlayer): Boolean;
    abstract fun afterUnlock(ecPlayer: ECPlayer)
}
