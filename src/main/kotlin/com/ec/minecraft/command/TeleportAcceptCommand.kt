package com.ec.minecraft.command

import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayerGameState
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "tpaccept",
    description = ["接受其他玩家传送到你的身边"]
)
internal class TeleportAcceptCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你接受要传送的玩家名称"]
    )
    var playerName: String = ""

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val target = Bukkit.getPlayer(playerName)
            ?: return sender.sendMessage(globalManager.message.system("玩家 $playerName 不存在！"))

        if (sender.name == target.name) {
            player.sendMessage(globalManager.message.system("您不能传送到你自己的位置。"))
            return
        }

        val ecPlayer = globalManager.players.getByPlayer(player)
        if (ecPlayer.gameState != ECPlayerGameState.FREE) {
            player.sendMessage(globalManager.message.system("您在活动状态无法进行传送！"))
            return
        }

        if (globalManager.states.teleportPlayers[target.name] == player.name) {
            globalManager.states.teleportPlayers.remove(target.name)
            target.teleportAsync(player.location)
            return
        }

        player.sendMessage("玩家 $playerName 并没有向你发送传送请求。")
    }

}