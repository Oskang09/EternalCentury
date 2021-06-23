package com.ec.manager.point

import com.ec.database.model.point.PointDetail
import com.ec.manager.GlobalManager
import org.bukkit.inventory.ItemStack

abstract class PointAPI(val id: String) {
    protected lateinit var globalManager: GlobalManager

    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun getItemStack(point: PointDetail): ItemStack
    abstract fun getGrade(point: PointDetail): Int;
}