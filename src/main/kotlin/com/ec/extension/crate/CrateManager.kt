package com.ec.extension.crate

import com.ec.config.CrateConfig
import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs

@Component
class CrateManager(
    @Inject("plugins/EternalCentury/crates")
    private val crateConfigs: MultiConfigs<CrateConfig>
) {
    private val crates: MutableMap<String, CrateConfig> = mutableMapOf()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        crateConfigs.getAll(true).subscribe {
            crates[it.path] = it.content
        }
    }

    fun getCrates(): List<CrateConfig> {
        return crates.values.toList()
    }
}