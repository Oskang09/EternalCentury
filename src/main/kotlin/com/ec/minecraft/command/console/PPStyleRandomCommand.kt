package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine

@CommandLine.Command(
    name = "pp-style-random",
    description = ["添加随机的附魔到指定玩家账号"]
)
class PPStyleRandomCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要给的玩家"],
    )
    var playerName: String = ""

    override fun execute() {
        requireSenderIsConsole()

        val style = globalManager.items.getRandomPPStyles()
        globalManager.givePlayerItem(playerName, listOf(style))
    }

}