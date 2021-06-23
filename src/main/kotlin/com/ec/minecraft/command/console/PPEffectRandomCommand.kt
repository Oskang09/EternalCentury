package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine

@CommandLine.Command(
    name = "pp-effect-random",
    description = ["添加随机的附魔到指定玩家账号"]
)
internal class PPEffectRandomCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要给的玩家"],
    )
    var playerName: String = ""

    override fun execute() {
        requireSenderIsConsole()

        val effect = globalManager.items.getRandomPPEffects()
        globalManager.givePlayerItem(playerName, listOf(effect))
    }

}