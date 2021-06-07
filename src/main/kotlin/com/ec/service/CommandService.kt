package com.ec.service

import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.extra.command.PicocliCommandService

@Component
class CommandService(private val commandService: PicocliCommandService) {

    fun onInitialize(globalManager: GlobalManager) {
    }


}