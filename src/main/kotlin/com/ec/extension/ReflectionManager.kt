package com.ec.extension

import com.ec.api.EndpointAPI
import com.ec.extension.enchantment.EnchantmentAPI
import com.ec.extension.inventory.UIProvider
import com.ec.extension.papi.PlaceholderAPI
import com.ec.extension.point.PointAPI
import com.ec.extension.title.TitleAPI
import com.ec.extension.ugui.ModuleAPI
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.extra.command.ReactantCommand
import org.reflections8.Reflections
import java.lang.reflect.Modifier

@Component
class ReflectionManager: LifeCycleHook {
    private lateinit var reflections: Reflections;

    override fun onEnable() {
        reflections = Reflections("com.ec")
    }

    fun loopCommands(action: (Class<out ReactantCommand>) -> Unit) {
        reflections.getSubTypesOf(ReactantCommand::class.java).forEach {
            action(it)
        }
    }

    fun loopModules(action: (ModuleAPI) -> Unit) {
        reflections.getSubTypesOf(ModuleAPI::class.java).forEach {
            action(it.getDeclaredConstructor().newInstance())
        }
    }

    fun loopEnchantments(action: (EnchantmentAPI) -> Unit) {
        reflections.getSubTypesOf(EnchantmentAPI::class.java).forEach {
            action(it.getDeclaredConstructor().newInstance())
        }
    }

    fun loopTitles(action: (TitleAPI) -> Unit) {
        reflections.getSubTypesOf(TitleAPI::class.java).forEach {
            action(it.getDeclaredConstructor().newInstance())
        }
    }

    fun loopPoints(action: (PointAPI) -> Unit) {
        reflections.getSubTypesOf(PointAPI::class.java).forEach {
            action(it.getDeclaredConstructor().newInstance())
        }
    }

    fun loopPlaceholders(action: (PlaceholderAPI) -> Unit) {
        reflections.getSubTypesOf(PlaceholderAPI::class.java).forEach {
            action(it.getDeclaredConstructor().newInstance())
        }
    }

    fun loopUI(action:(UIProvider<*>) -> Unit) {
        reflections.getSubTypesOf(UIProvider::class.java).forEach {
            if (!Modifier.isAbstract(it.modifiers)) {
                action(it.getDeclaredConstructor().newInstance())
            }
        }
    }

    fun loopEndpoints(action: (EndpointAPI) -> Unit) {
        reflections.getSubTypesOf(EndpointAPI::class.java).forEach {
            action(it.getDeclaredConstructor().newInstance())
        }
    }

}