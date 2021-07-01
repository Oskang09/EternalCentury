package com.ec.minecraft.command

import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayerGameState
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "home",
    description = ["设置家里坐标"]
)
internal class HomeCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        arity = "0..1",
        paramLabel = "家园名称",
        description = ["你要的家园名称"],
    )
    var homeName: String = "default"

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val ecPlayer = globalManager.players.getByPlayer(player)
        if (ecPlayer.gameState == ECPlayerGameState.ACTIVITY) {
            player.sendMessage(globalManager.message.system("您在活动状态无法进行传送！"))
            return
        }
        val homes = globalManager.states.getPlayerState(player).homes
        val home = homes[homeName]
        if (home == null) {
            player.sendMessage(globalManager.message.system("您是不是没有设置还没设置家园?"))
            return
        }
        player.teleportAsync(home.location)
    }

}