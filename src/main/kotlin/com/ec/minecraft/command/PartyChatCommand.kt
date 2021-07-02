package com.ec.minecraft.command

import com.ec.database.enums.ChatType
import com.ec.manager.GlobalManager
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "pc",
    aliases = ["partychat", "party-chat"],
    description = ["发送队伍聊天讯息"]
)
internal class PartyChatCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0..*",
        paramLabel = "讯息",
        description = ["你要发送的讯息"]
    )
    var message: Array<String> = arrayOf()

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val prefix = globalManager.message.playerChatPrefix(ChatType.PARTY)
        val componentMessage = prefix.append("&r ".toComponent()).append(player.displayName()).append("&r : ".toComponent()).append(message.joinToString(" ").toComponent())

        globalManager.mcmmo.getPlayerParty(player).map { p -> p.sendMessage(componentMessage) }
        Bukkit.getConsoleSender().sendMessage(componentMessage)
    }

}