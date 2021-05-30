package com.ec.extension

import com.ec.extension.enchantment.EnchantmentAPI
import com.ec.extension.inventory.UIProvider
import com.ec.extension.papi.PlaceholderAPI
import com.ec.extension.point.PointAPI
import com.ec.extension.title.TitleAPI
import com.ec.extension.trait.TraitAPI
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import org.reflections8.Reflections

@Component
class ReflectionManager: LifeCycleHook {
    private lateinit var reflections: Reflections;

    override fun onEnable() {
        reflections = Reflections("com.ec.minecraft")
    }

    fun loopEnchantments(action: (EnchantmentAPI) -> Unit) {
        reflections.getSubTypesOf(EnchantmentAPI::class.java).forEach {
            action(it.newInstance())
        }
    }

    fun loopTitles(action: (TitleAPI) -> Unit) {
        reflections.getSubTypesOf(TitleAPI::class.java).forEach {
            action(it.newInstance())
        }
    }

    fun loopPoints(action: (PointAPI) -> Unit) {
        reflections.getSubTypesOf(PointAPI::class.java).forEach {
            action(it.newInstance())
        }
    }

    fun loopPlaceholders(action: (PlaceholderAPI) -> Unit) {
        reflections.getSubTypesOf(PlaceholderAPI::class.java).forEach {
            action(it.newInstance())
        }
    }

    fun loopUI(action:(UIProvider) -> Unit) {
        reflections.getSubTypesOf(UIProvider::class.java).forEach {
            action(it.newInstance())
        }
    }

    fun loopTraits(action:(Class<out TraitAPI>) -> Unit) {
        reflections.getSubTypesOf(TraitAPI::class.java).forEach {
            action(it)
        }
    }

}