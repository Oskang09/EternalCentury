package com.ec.extension.ugui

import com.ec.extension.GlobalManager
import me.oska.module.ModuleInformation

abstract class ModuleAPI: ModuleInformation() {

    protected lateinit var globalManager: GlobalManager

    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    override fun getAuthor(): String {
        return "Oska"
    }

    override fun getVersion(): String {
        return "0.0.1"
    }

    override fun isSupported() {

    }

}