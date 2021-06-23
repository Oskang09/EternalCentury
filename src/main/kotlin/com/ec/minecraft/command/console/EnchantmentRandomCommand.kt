package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine

@CommandLine.Command(
    name = "enchantment-random",
    description = ["添加随机的附魔到指定玩家账号"]
)
internal class EnchantmentRandomCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要收到/无视的玩家名称"],
    )
    var playerName: String = ""

    @CommandLine.Parameters(
        index = "1",
        paramLabel = "附魔数量",
        description = ["你要给的附魔数量"]
    )
    var numOfEnchantments: Int = 0

    @CommandLine.Parameters(
        index = "2",
        paramLabel = "等级",
        description = ["最高可给的等级"]
    )
    var maxLevel: Int = 0

    override fun execute() {
        requireSenderIsConsole()

        val ench = globalManager.enchantments.getRandomEnchantedBook(numOfEnchantments, maxLevel)
        globalManager.givePlayerItem(playerName, listOf(ench))
    }
}