package com.ec.minecraft.command.console

import com.ec.database.Players
import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import picocli.CommandLine
import java.util.*

@CommandLine.Command(
    name = "res-add",
    description = ["添加玩家账号可用领地数"]
)
internal class ResidenceAddCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要添加的玩家名称"],
    )
    var playerName: String? = null

    override fun execute() {
        requireSenderIsConsole()

        val player = globalManager.players.getByPlayerName(playerName!!)!!
        transaction {
            Players.update({ Players.id eq player[Players.id] }) {
                it[resLimit] = player[resLimit] + 1
            }

            globalManager.players.refreshPlayerIfOnline(UUID.fromString(player[Players.uuid])) {
                globalManager.permission.injectPermission(it)
            }
        }
    }
}