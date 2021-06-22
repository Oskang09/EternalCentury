package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import com.ec.util.DoubleUtil.roundTo
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine
import kotlin.random.Random

@CommandLine.Command(
    name = "balance-random",
    description = ["添加随机的金钱到指定玩家账号"]
)
internal class BalanceRandomCommand(private val globalManager: GlobalManager): ReactantCommand() {

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
    var min: Double? = null

    @CommandLine.Parameters(
        index = "2",
        paramLabel = "金钱",
        description = ["你要给的金钱"]
    )
    var max: Double? = null

    override fun execute() {
        requireSenderIsConsole()

        val balance = Random.nextDouble(min!!, max!!).roundTo(2)
        globalManager.economy.depositPlayer(Bukkit.getPlayer(playerName!!)!!, balance)
    }

}