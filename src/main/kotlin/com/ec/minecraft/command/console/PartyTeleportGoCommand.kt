package com.ec.minecraft.command.console

import com.ec.extension.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "ptg",
    description = ["队伍传送"]
)
internal class PartyTeleportGoCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要传送的玩家名称"],
    )
    var playerName: String? = null

    @CommandLine.Parameters(
        index = "1",
        paramLabel = "传送点",
        description = ["你要传送的玩家名称"],
    )
    var teleportTo: String = ""


    override fun execute() {

    }

}