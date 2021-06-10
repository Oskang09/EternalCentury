package com.ec.extension.command

import com.ec.extension.GlobalManager
import com.ec.minecraft.command.*
import com.ec.minecraft.command.console.BalanceAddCommand
import com.ec.minecraft.command.console.PermissionAddCommand
import com.ec.minecraft.command.console.PointAddCommand
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
            command({ TeleportCommand(globalManager) })
            command({ TeleportAcceptCommand(globalManager) })
            command({ TeleportBlockCommand(globalManager) })
            command({ PrivateMessageCommand(globalManager) })
            command({ IgnoreCommand(globalManager) })
            command({ BalanceCommand(globalManager) })

            // console only
            command({ BalanceAddCommand(globalManager) })
            command({ PermissionAddCommand(globalManager) })
            command({ PointAddCommand(globalManager) })
        }

    }

}