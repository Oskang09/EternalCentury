package com.ec.minecraft.command

import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayerGameState
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "spawn",
    description = ["回到主城"]
)
internal class SpawnCommand(private val globalManager: GlobalManager): ReactantCommand() {

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val ecPlayer = globalManager.players.getByPlayer(player)
        if (ecPlayer.gameState == ECPlayerGameState.ACTIVITY) {
            player.sendMessage(globalManager.message.system("您在活动状态无法进行传送！"))
            return
        }

        player.teleportAsync(globalManager.serverConfig.teleports["old-spawn"]!!.location)
    }
}