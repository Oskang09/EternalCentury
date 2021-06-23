package com.ec.minecraft.command

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "wb",
    description = ["打开工作台"]
)
class WorkBenchCommand(private val globalManager: GlobalManager): ReactantCommand() {

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        if (!player.hasPermission("ec.workbench")) {
            player.sendMessage(globalManager.message.system("您还未解锁此功能，请到商店购买权限后才能使用。"))
            return
        }
        player.openWorkbench(player.location, true)
    }

}