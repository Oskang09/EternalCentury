package com.ec.minecraft.command

import com.ec.database.Malls
import com.ec.database.Players
import com.ec.extension.GlobalManager
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Material
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import picocli.CommandLine
import java.time.Instant

@CommandLine.Command(
    name = "sell",
    description = ["拍卖物品到商场"]
)
internal class SellCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "价格",
        description = ["您要拍卖的商场的价格"]
    )
    var inputPrice: Double = 0.0

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val handItem = player.inventory.itemInMainHand
        val ecPlayer = globalManager.players.getByPlayer(player)
        val count = transaction {
            return@transaction Malls.select { Malls.playerId eq ecPlayer.database[Players.id] }.count()
        }

        if (count >= ecPlayer.database[Players.auctionLimit]) {
            player.sendMessage(globalManager.message.system("您无法再拍卖更多的物品，已经达到上限了。"))
            return
        }

        if (handItem.type == Material.AIR) {
            player.sendMessage(globalManager.message.system("您手上没有任何东西，无法放入市场拍卖。"))
            return
        }

        val nbtItem = globalManager.items.deserializeFromItem(handItem)
        player.inventory.remove(handItem)
        transaction {

            Malls.insert {
                it[id] = "".generateUniqueID()
                it[playerId] = ecPlayer.database[Players.id]
                it[playerName] = player.name
                it[material] = handItem.type
                it[amount] = handItem.amount
                it[item] = handItem
                it[price] = inputPrice
                it[createdAt] = Instant.now().epochSecond
                if (nbtItem != null && nbtItem.id != "") {
                    it[nativeId] = nbtItem.id
                }
            }
        }

        player.sendMessage(globalManager.message.system("物品上架成功。"))
    }
}