package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine

@CommandLine.Command(
    name = "tpgo",
    description = ["传送到管理员专用传送点"]
)
internal class TeleportGoCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要传送的玩家名称"],
    )
    var playerName: String? = null

    @CommandLine.Parameters(
        index = "1",
        paramLabel = "传送点",
        description = ["你要传送的玩家名称"],
    )
    var teleportTo: String = ""

    override fun execute() {
        requireSenderIsConsole()

        val target = Bukkit.getPlayer(playerName!!)
        if (target == null) {
            sender.sendMessage(globalManager.message.system("该玩家不存在."))
            return
        }

        val location = globalManager.serverConfig.teleports[teleportTo]
        if (location == null) {
            target.sendMessage(globalManager.message.system("传送点不存在, 出现了问题快告诉服主."))
            return
        }

        target.teleport(location)
    }

}