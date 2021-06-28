package com.ec.minecraft.command

import com.ec.database.Players
import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.update
import picocli.CommandLine

@CommandLine.Command(
    name = "ignore",
    description = ["收到/无视玩家的任何讯息"]
)
internal class IgnoreCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要收到/无视的玩家名称"]
    )
    var playerName: String = ""

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val target = Bukkit.getPlayer(playerName)
            ?: return sender.sendMessage(globalManager.message.system("玩家 $playerName 不存在！"))

        if (sender.name == target.name) {
            player.sendMessage(globalManager.message.system("您不能禁止您自己的讯息。"))
            return
        }

        if (globalManager.serverConfig.adminPlayers.contains(target.name)) {
            player.sendMessage(globalManager.message.system("嘿嘿，小坏蛋还想无视管理员的讯息？"))
            return
        }

        val ecPlayer = globalManager.players.getByPlayer(player)
        ecPlayer.ensureUpdate("ignore command update", isAsync = true) {
            if (ecPlayer.database[Players.ignoredPlayers].contains(target.uniqueId.toString())) {
                ecPlayer.database[Players.ignoredPlayers].remove(target.uniqueId.toString())
                player.sendMessage(globalManager.message.system("玩家 ${target.name} 可以向你发送讯息了。"))
            } else {
                ecPlayer.database[Players.ignoredPlayers].add(target.uniqueId.toString())
                player.sendMessage(globalManager.message.system("玩家 ${target.name} 将无法向您发送讯息了。"))
            }

            Players.update({ Players.id eq ecPlayer.database[Players.id] }) {
                it[ignoredPlayers] = ecPlayer.database[ignoredPlayers]
            }
        }
    }

}