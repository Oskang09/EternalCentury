package com.ec.minecraft.command.console

import com.ec.manager.GlobalManager
import dev.reactant.reactant.extra.command.ReactantCommand
import me.oska.config.shop.ItemConfig
import org.bukkit.inventory.ItemStack
import picocli.CommandLine

@CommandLine.Command(
    name = "random-item",
    description = ["添加随机的物品到指定玩家账号"]
)
internal class RandomItemCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        index = "0",
        paramLabel = "玩家名称",
        description = ["你要给的玩家"],
    )
    var playerName: String = ""

    @CommandLine.Parameters(
        index = "1",
        paramLabel = "物品类型",
        description = ["你要给的物品类型"],
    )
    var itemType: String = ""

    @CommandLine.Parameters(
        index = "2",
        paramLabel = "物品数量",
        description = ["你要给的数量"],
    )
    var itemCount: Int = 1

    override fun execute() {
        requireSenderIsConsole()

        ItemConfig

        val items = mutableListOf<ItemStack>()
        when (itemType) {
            "egg" -> repeat(itemCount) {
                items.add(globalManager.items.getRandomEgg())
            }
            "arrow" -> repeat(itemCount) {
                items.add(globalManager.items.getRandomArrow())
            }
        }

        globalManager.givePlayerItem(playerName, items)
    }

}