package com.ec.minecraft.command

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "delhome",
    description = ["设置家里坐标"]
)
internal class DeleteHomeCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        arity = "0..1",
        paramLabel = "家园名称",
        description = ["你要的家园名称"],
    )
    var homeName: String? = null

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val name = homeName ?: "default"
        val homes = globalManager.states.getStateConfig(player).homes
        val home = homes[name]
        if (home == null) {
            player.sendMessage(globalManager.message.system("您没有家园名为$name。"))
            return
        }

        globalManager.states.updateStateConfig(player) {
            it.homes.remove(name)
        }
        player.sendMessage(globalManager.message.system("家园 $name 已经被删除了！"))
    }

}