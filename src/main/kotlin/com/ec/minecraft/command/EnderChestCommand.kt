package com.ec.minecraft.command

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "ec",
    description = ["打开自己的末影盒子"]
)
internal class EnderChestCommand(private val globalManager: GlobalManager): ReactantCommand() {

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        if (!player.hasPermission("ec.enderchest")) {
            player.sendMessage(globalManager.message.system("您还未解锁此功能，请到商店购买权限后才能使用。"))
            return
        }
        player.openInventory(player.enderChest)
    }

}