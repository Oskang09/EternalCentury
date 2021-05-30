package com.ec.extension.papi

import com.ec.ECCore
import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

@Component
class PlaceholderManager: PlaceholderExpansion() {
    private lateinit var globalManager: GlobalManager
    private val placeholders: MutableMap<String, PlaceholderAPI> = HashMap()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopPlaceholders {
            it.initialize(globalManager)
            placeholders[it.placeholderKey()] = it
        }

        register()
    }

    override fun getIdentifier(): String {
        return "ec"
    }

    override fun getAuthor(): String {
        return ECCore.instance.description.authors.toString()
    }

    override fun getVersion(): String {
        return ECCore.instance.description.version
    }

    override fun onPlaceholderRequest(player: Player, identifier: String): String {
        val item = placeholders[identifier]
        if (item != null) {
            return item.onPlaceholderRequest(player)
        }
        return ""
    }
}