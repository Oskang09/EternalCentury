package com.ec.minecraft.command

import com.ec.database.Players
import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "pm",
    aliases = ["private-message"],
    description = ["向玩家发送私聊讯息"]
)
internal class PrivateMessageCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要发送私聊的玩家"]
    )
    var playerName: String = ""

    @CommandLine.Parameters(
        index = "1..*",
        paramLabel = "讯息",
        description = ["你要发送的讯息"]
    )
    var message: Array<String> = arrayOf()

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val target = Bukkit.getPlayer(playerName)
            ?: return sender.sendMessage(globalManager.message.system("玩家 $playerName 不存在！"))

        if (sender.name == target.name) {
            player.sendMessage(globalManager.message.system("您不能发送讯息给您自己。"))
            return
        }

        val ecPlayer = globalManager.players.getByPlayer(target)
        if (ecPlayer.database[Players.blockedTeleport].contains(player.uniqueId.toString())) {
            player.sendMessage(globalManager.message.system("该玩家已经把你加入了聊天室黑名单。"))
            return
        }
        target.sendMessage(globalManager.message.private(player, message.joinToString(" ")))
    }
}