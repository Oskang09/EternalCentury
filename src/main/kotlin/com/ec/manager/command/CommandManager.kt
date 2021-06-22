package com.ec.manager.command

import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.extra.command.PicocliCommandService

@Component
class CommandManager(
    private val commandService: PicocliCommandService,
    private val globalManager: GlobalManager,
): LifeCycleHook {

    override fun onEnable() {

        commandService {
            globalManager.reflections.loopCommands {
                val constructedCommand = it.getDeclaredConstructor(GlobalManager::class.java).newInstance(globalManager)
                command({ constructedCommand })
            }
        }

    }

}