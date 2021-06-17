package com.ec.minecraft.command.console

import com.ec.extension.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "reload-config",
    description = ["刷新伺服端的配置文件"]
)
internal class ReloadServerConfigCommand(private val globalManager: GlobalManager): ReactantCommand()  {

    override fun execute() {
        requireSenderIsConsole()

        globalManager.reloadServerConfig()
    }

}