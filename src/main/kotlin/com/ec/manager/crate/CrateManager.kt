package com.ec.manager.crate

import com.ec.config.CrateConfig
import com.ec.manager.GlobalManager
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
            crates[it.content.id] = it.content
        }
    }

    fun getCrateById(crate: String): CrateConfig {
        return crates[crate]!!
    }

    fun getCrates(): List<CrateConfig> {
        return crates.values.toList()
    }
}