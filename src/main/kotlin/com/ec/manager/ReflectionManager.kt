package com.ec.manager

import com.ec.api.EndpointAPI
import com.ec.manager.activity.ActivityAPI
import com.ec.manager.enchantment.EnchantmentAPI
import com.ec.manager.inventory.UIProvider
import com.ec.manager.papi.PlaceholderAPI
import com.ec.manager.skill.SkillAPI
import com.ec.manager.wallet.WalletAPI
import com.ec.manager.title.TitleAPI
import com.ec.manager.ugui.ModuleAPI
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

    fun loopSkills(action: (Class<out SkillAPI>) -> Unit) {
        reflections.getSubTypesOf(SkillAPI::class.java).forEach {
            action(it)
        }
    }

    fun loopActivity(action: (Class<out ActivityAPI>) -> Unit) {
        reflections.getSubTypesOf(ActivityAPI::class.java).forEach {
            action(it)
        }
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

    fun loopWallets(action: (WalletAPI) -> Unit) {
        reflections.getSubTypesOf(WalletAPI::class.java).forEach {
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