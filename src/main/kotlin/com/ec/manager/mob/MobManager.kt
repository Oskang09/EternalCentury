package com.ec.manager.mob

import com.ec.config.mobs.MobConfig
import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs

@Component
class MobManager(
    @Inject("plugins/EternalCentury/mobs")
    private val mobsFiles: MultiConfigs<MobConfig>
) {
    private val mobs = mutableMapOf<String, IEntity>()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        mobsFiles.getAll(true).subscribe {
            val config = it.content
            mobs[config.id] = IEntity(globalManager, config)
        }
    }

}