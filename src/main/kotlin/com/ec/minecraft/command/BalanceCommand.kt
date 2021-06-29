package com.ec.minecraft.command

import com.ec.database.Players
import com.ec.manager.GlobalManager
import com.ec.manager.wallet.WalletManager
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
    name = "balance",
    aliases = ["bal"],
    description = ["查看自己的目前金钱"]
)
internal class BalanceCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        arity = "0..1",
        paramLabel = "玩家名称",
        description = ["你要收到/无视的玩家名称"],
    )
    var playerName: String? = null

    override fun execute() {
        if (playerName == null) {
            requireSenderIsPlayer()

            val player = sender as Player
            val wallet = globalManager.wallets.playerWallet(player.name, WalletManager.ECONOMY_WALLET)
            player.sendMessage(globalManager.message.system("金钱 ： &e&l${wallet.balance}"))
            return
        }

        val target = Bukkit.getPlayer(playerName!!)
            ?: return sender.sendMessage(globalManager.message.system("玩家 $playerName 不存在！"))

        if (sender.name != target.name) {
            if (!globalManager.serverConfig.adminPlayers.contains(target.name)) {
                sender.sendMessage(globalManager.message.system("嘿嘿，小坏蛋您不能偷看别人有多少金钱哦。"))
                return
            }
        }

        val wallet = globalManager.wallets.playerWallet(target.name, WalletManager.ECONOMY_WALLET)
        sender.sendMessage(globalManager.message.system("${target.name}的金钱 ： &e&l${wallet.balance}"))
    }
}