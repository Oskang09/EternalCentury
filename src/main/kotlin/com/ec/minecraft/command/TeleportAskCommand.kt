package com.ec.minecraft.command

import com.ec.database.Players
import com.ec.manager.GlobalManager
import com.ec.model.ObservableMapActionType
import com.ec.model.player.ECPlayerGameState
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "tpa",
    description = ["传送到指定的玩家身边"]
)
internal class TeleportAskCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要传送的玩家名称"]
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

        val targetPlayer = globalManager.players.getByPlayer(target)
        if (targetPlayer.database[Players.blockedTeleport].contains(player.uniqueId.toString())) {
            player.sendMessage(globalManager.message.system("玩家 ${target.name} 已经把你加入传送黑名单了。"))
            return
        }

        if (globalManager.serverConfig.adminPlayers.contains(player.name)) {
            player.teleportAsync(target.location)
            return
        }

        player.sendMessage(globalManager.message.system("您的传送请求已经发送到 ${target.name} 了。"))
        target.sendMessage(globalManager.message.system("${player.name} 想传送到你这边，接受的话请输入或者点我 <insert:/tpa accept ${player.name}>/tpaccept ${player.name}</insert>。"))
        globalManager.states.teleportPlayers[sender.name] = target.name

        val task = globalManager.states.delayedTask(10) {
            if (globalManager.states.teleportPlayers.remove(sender.name) != null) {
                player.sendMessage(globalManager.message.system("您的的传送请求到 ${target.name} 已经过期了。"))
                target.sendMessage(globalManager.message.system("${player.name} 的传送请求已经过期了。"))
            }
        }

        globalManager.states.teleportPlayers.subscribeOnce({ it.type == ObservableMapActionType.REMOVE && it.key == sender.name }) {
            globalManager.states.disposeTask(task)
        }
    }
}