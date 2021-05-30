package com.ec.extension.trait

import com.ec.extension.GlobalManager
import net.citizensnpcs.api.trait.Trait

abstract class TraitAPI(val id: String): Trait(id) {
    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }
}