package com.ec.minecraft.command.admin

import com.ec.extension.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "admin-ui",
    aliases = ["aui"],
    description = ["开启管理员界面"]
)
internal class AdminUICommand(private val globalManager: GlobalManager): ReactantCommand() {

    override fun execute() {
        requireSenderIsPlayer()

        if (!globalManager.serverConfig.adminPlayers.contains(sender.name)) {
            sender.sendMessage(globalManager.message.system("你个小兔崽子你是怎么找到的？"))
            return
        }

        globalManager.inventory.displayAdmin(sender as Player)
    }


}