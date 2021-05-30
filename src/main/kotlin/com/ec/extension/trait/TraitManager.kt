package com.ec.extension.trait

import com.ec.extension.GlobalManager
import com.ec.minecraft.trait.RepairTrait
import dev.reactant.reactant.core.component.Component
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.trait.TraitInfo

@Component
class TraitManager {

    fun onInitialize(globalManager: GlobalManager) {
        globalManager.reflections.loopTraits {
            val trait = it.newInstance()
            trait.initialize(globalManager)
            CitizensAPI.getTraitFactory().registerTrait(
                TraitInfo.create(it).withName(trait.id).withSupplier {
                    return@withSupplier trait
                }
            )
        }
    }

}