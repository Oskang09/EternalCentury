package com.ec.minecraft.command.console

import com.ec.extension.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine

@CommandLine.Command(
    name = "balance-add",
    aliases = ["badd"],
    description = ["添加金钱到指定玩家账号"]
)
internal class BalanceAddCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要收到/无视的玩家名称"],
    )
    var playerName: String? = null

    @CommandLine.Parameters(
        index = "1",
        paramLabel = "金钱",
        description = ["你要给的金钱"]
    )
    var money: Double? = null

    override fun execute() {
        requireSenderIsConsole()

        globalManager.economy.depositPlayer(Bukkit.getPlayer(playerName!!)!!, money!!)
    }

}