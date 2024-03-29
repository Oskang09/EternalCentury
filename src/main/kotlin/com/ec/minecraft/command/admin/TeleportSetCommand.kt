package com.ec.minecraft.command.admin

import com.ec.config.wrapper.LocationConfig
import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "tpset",
    description = ["设置管理员专用传送点"]
)
internal class TeleportSetCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "传送点",
        description = ["传送点名称"],
    )
    var name: String = ""

    override fun execute() {
        requireSenderIsPlayer()

        if (!globalManager.serverConfig.adminPlayers.contains(sender.name)) {
            sender.sendMessage(globalManager.message.system("你个小兔崽子你是怎么找到的？"))
            return
        }

        val player = sender as Player
        globalManager.serverConfig.teleports[name] = LocationConfig(player.location)
        globalManager.saveServerConfig()
    }

}