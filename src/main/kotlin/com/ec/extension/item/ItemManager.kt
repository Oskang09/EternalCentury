package com.ec.extension.item

import com.ec.config.ItemData
import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs

@Component
class ItemManager(
    @Inject("plugins/server-data/items")
    private val itemConfigs: MultiConfigs<ItemData>
) {

    private val items: MutableList<ItemData> = mutableListOf()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        itemConfigs.getAll(true).forEach {
            items.add(it.content)
        }
    }

}