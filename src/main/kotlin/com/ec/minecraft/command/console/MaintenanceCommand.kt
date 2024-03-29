package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine

@CommandLine.Command(
    name = "maintain",
    aliases = ["maintain"],
    description = ["开启或者关闭维修模式"]
)
internal class MaintenanceCommand(private val globalManager: GlobalManager): ReactantCommand() {

    override fun execute() {
        requireSenderIsConsole()

        globalManager.serverConfig.maintenance = !globalManager.serverConfig.maintenance
        globalManager.discord.updateServerInfo(globalManager.serverConfig.maintenance)
        globalManager.saveServerConfig()
        if (globalManager.serverConfig.maintenance) {
            globalManager.discord.broadcastToGameChannel("\uD83D\uDEE0 **伺服器维修中**")

            Bukkit.getOnlinePlayers().forEach {
                if (!globalManager.serverConfig.adminPlayers.contains(it.name)) {
                    it.player?.kick(globalManager.message.system("&f[&5系统&f] &f伺服器维修中"))
                }
            }
        } else {
            globalManager.discord.broadcastToGameChannel("✅ **伺服器开启了**")
        }
    }

}