package com.ec.extension.papi

import com.ec.ECCore
import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@Component
class PlaceholderManager: PlaceholderExpansion() {
    private lateinit var globalManager: GlobalManager
    private val placeholders: MutableMap<String, PlaceholderAPI> = HashMap()
    private val dynamicPlaceholders: MutableMap<String, PlaceholderAPI> = HashMap()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopPlaceholders {
            it.initialize(globalManager)
            if (it.isDynamic()) {
                dynamicPlaceholders[it.placeholderKey()] = it
            } else {
                placeholders[it.placeholderKey()] = it
            }
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
            return item.onPlaceholderRequest(player, identifier)
        }

        dynamicPlaceholders.forEach {
            if (identifier.startsWith(it.key)) {
                return it.value.onPlaceholderRequest(player, identifier.replace(it.key, ""))
            }
        }
        return ""
    }
}