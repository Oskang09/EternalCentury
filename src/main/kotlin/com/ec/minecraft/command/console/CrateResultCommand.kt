package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import com.ec.util.ChanceUtil
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import picocli.CommandLine

@CommandLine.Command(
    name = "crate-result",
    description = ["给予玩家抽奖奖励"]
)
class CrateResultCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要添加的玩家名称"],
    )
    var playerName: String? = null

    @CommandLine.Parameters(
        index = "1",
        paramLabel = "抽奖名称",
        description = ["你要抽奖的名称"],
    )
    var crateName: String? = null

    override fun execute() {
        requireSenderIsConsole()

        val crate = globalManager.crates.getCrateById(crateName!!)
        val player = Bukkit.getPlayer(playerName!!)
        if (player != null && player.isOnline) {
            val reward = mutableListOf(crate.rewards.random())
            globalManager.discord.broadcast("恭喜 $playerName 在抽奖中获得了%item%!", globalManager.items.getItem(reward[0].item))

            if (ChanceUtil.defaultChance(10)) {
                reward.add(crate.rewards.random())
                globalManager.discord.broadcast("恭喜 $playerName 触发了抽奖连击，额外获得了%item%!", globalManager.items.getItem(reward[1].item))
            }

            globalManager.sendRewardToPlayer(player, reward.map { it.reward }.flatten())
        }
    }

}