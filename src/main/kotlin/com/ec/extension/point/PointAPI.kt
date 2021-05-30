package com.ec.extension.point

import com.ec.extension.GlobalManager

abstract class PointAPI(val id: String) {
    protected lateinit var globalManager: GlobalManager

    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun getGrade(point: Int): Int;
}