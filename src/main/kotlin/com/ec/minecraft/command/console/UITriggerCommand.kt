package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import org.bukkit.inventory.meta.Damageable
import picocli.CommandLine

@CommandLine.Command(
    name = "ui-trigger",
    description = ["强制玩家开启指定UI"]
)
class UITriggerCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "UI名称",
        description = ["你要开启的UI"]
    )
    var uiName: String = ""

    @CommandLine.Parameters(
        index = "1",
        paramLabel = "玩家名称",
        description = ["你要开启UI的玩家"]
    )
    var playerName: String = ""

    override fun execute() {
        requireSenderIsConsole()

        val player = Bukkit.getPlayer(playerName)!!
        when (uiName) {
            "repair" -> {
                val item = player.inventory.itemInMainHand
                if (item.hasItemMeta() && item.itemMeta is Damageable) {
                    return globalManager.inventory.displayRepair(player)
                }
                player.sendMessage(globalManager.message.system("您的物品暂时不需要修理"))
            }
            "auction" -> globalManager.inventory.displayAuction(player)
        }
    }
}