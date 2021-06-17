package com.ec.minecraft.command.console

import com.ec.extension.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine

@CommandLine.Command(
    name = "permission-add",
    description = ["添加权限到指定玩家账号"]
)
internal class PermissionAddCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要收到/无视的玩家名称"],
    )
    var playerName: String? = null

    @CommandLine.Parameters(
        index = "1",
        paramLabel = "权限",
        description = ["你要给的权限"]
    )
    var permission: String? = null

    override fun execute() {
        requireSenderIsConsole()

        globalManager.permission.playerAdd(null, Bukkit.getPlayer(playerName!!), permission)
    }

}