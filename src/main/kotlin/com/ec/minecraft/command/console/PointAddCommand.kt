package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine

@CommandLine.Command(
    name = "point-add",
    description = ["添点数限到指定玩家账号"]
)
internal class PointAddCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要收到/无视的玩家名称"],
    )
    var playerName: String? = null

    @CommandLine.Parameters(
        index = "1",
        paramLabel = "点数",
        description = ["你要给的点数"]
    )
    var pointName: String? = null

    @CommandLine.Parameters(
        index = "2",
        paramLabel = "数量",
        description = ["你要给的点数数量"]
    )
    var pointCount: Double? = null

    override fun execute() {
        requireSenderIsConsole()

        globalManager.points.depositPlayerPoint(Bukkit.getPlayer(playerName!!)!!, pointName!!, pointCount!!)
    }

}