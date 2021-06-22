package com.ec.minecraft.command

import com.ec.database.Players
import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.update
import picocli.CommandLine

@CommandLine.Command(
    name = "tpb",
    aliases = ["teleport-block"],
    description = ["允许/禁止玩家向你发送传送请求"]
)
internal class TeleportBlockCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你允许/禁止发送传送请求的玩家名称"]
    )
    var playerName: String = ""

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val target = Bukkit.getPlayer(playerName)
            ?: return sender.sendMessage(globalManager.message.system("玩家 $playerName 不存在！"))

        if (sender.name == target.name) {
            player.sendMessage(globalManager.message.system("您不能禁止您自己的请求。"))
            return
        }

        if (globalManager.serverConfig.adminPlayers.contains(target.name)) {
            player.sendMessage(globalManager.message.system("嘿嘿，小坏蛋还想禁止管理员的请求？"))
            return
        }

        val ecPlayer = globalManager.players.getByPlayer(player)
        ecPlayer.ensureUpdate("teleport block command update", isAsync = true) {
            if (ecPlayer.database[Players.blockedTeleport].contains(target.name)) {
                ecPlayer.database[Players.blockedTeleport].remove(target.name)
                player.sendMessage(globalManager.message.system("玩家 ${target.name} 可以向您发送传送请求了。"))
            } else {
                ecPlayer.database[Players.blockedTeleport].add(target.name)
                player.sendMessage(globalManager.message.system("玩家 ${target.name} 将无法向您发送传送请求了。"))
            }

            Players.update({ Players.id eq ecPlayer.database[Players.id] }) {
                it[blockedTeleport] = ecPlayer.database[blockedTeleport]
            }
        }
    }
}