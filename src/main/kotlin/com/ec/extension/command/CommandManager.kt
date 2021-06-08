package com.ec.extension.command

import com.ec.extension.GlobalManager
import com.ec.minecraft.command.PrivateMessageCommand
import com.ec.minecraft.command.TeleportAcceptCommand
import com.ec.minecraft.command.TeleportBlockCommand
import com.ec.minecraft.command.TeleportCommand
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
//            command({ TeleportUnblockCommand(globalManager) })
        }

    }

}