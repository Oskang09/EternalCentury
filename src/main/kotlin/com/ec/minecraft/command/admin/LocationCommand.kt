package com.ec.minecraft.command.admin

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "location",
    description = ["获取目标方块的位置讯息"]
)
internal class LocationCommand(private val globalManager: GlobalManager): ReactantCommand() {

    override fun execute() {
        requireSenderIsPlayer()

        if (!globalManager.serverConfig.adminPlayers.contains(sender.name)) {
            sender.sendMessage(globalManager.message.system("你个小兔崽子你是怎么找到的？"))
            return
        }

        val player = sender as Player
        val block = player.getTargetBlock(10)!!
        player.sendMessage {
            val x = block.location.blockX
            val y = block.location.blockY
            val z = block.location.blockZ
            globalManager.message.system("W: ${block.location.world.name} &fX: &e$x, &fY: &e$y, &fZ: &e$z")
        }
    }

}