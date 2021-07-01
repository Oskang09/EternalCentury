package com.ec.minecraft.command

import com.ec.config.wrapper.LocationConfig
import com.ec.database.Players
import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayerGameState
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "sethome",
    description = ["设置家园坐标"]
)
internal class SetHomeCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0..1",
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
        if (!homes.keys.contains(homeName) && homes.keys.size + 1 > ecPlayer.database[Players.homeLimit]) {
            player.sendMessage(globalManager.message.system("您的家园数量已达上限！"))
            return
        }

        homes[homeName] = LocationConfig(player.location)
        player.sendMessage(globalManager.message.system("您的家园设置成功！"))
    }

}