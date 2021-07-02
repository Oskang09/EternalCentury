package com.ec.minecraft.command

import com.ec.manager.GlobalManager
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "rules",
    description = ["查看规则"]
)
internal class RulesCommand(private val globalManager: GlobalManager): ReactantCommand() {

    override fun execute() {
        requireSenderIsPlayer()

        sender.sendMessage((
            "&f&l简单解释（”最终决定权“）\n" +
            "&f- 伺服器会对该玩家做出的惩罚，全部源自伺服器的决定而不是玩家们的决定。\n" +
            "&f- 一旦做出决定此事情当作已解决，若再犯也会增加处罚。\n" +
            "\n" +
            "&f&l1. 禁止制造任何能造成伺服卡顿的机关 （红石连闪，高空水流，高空岩浆）\n" +
            "&f&l2. 伺服管理员拥有最终的决定权在于对玩家之间的纷争问题\n" +
            "&f&l3. 不要与其他玩家在大厅频道吵架，可以的话私下聊（伺服管理员也有最终决定权）\n" +
            "&f&l4. 不要刷频造成其他人的困扰，若其他玩家回报（伺服管理员也有最终决定权）\n" +
            "&f&l5. 有BUG请立即回报管理员，任何非法用BUG刷物品找到会会直接封锁\n" +
            "&f&l6. 禁止使用外挂，若其他玩家回报（伺服器也有最终决定权）\n" +
            "&f&l7. 禁止蓄意破坏其他玩家的物品 / 建组 （目前有领地，除了BUG之外是破坏不了）\n" +
            "&f&l8. 禁止欺骗玩家的财务与物品，若其他玩家回报并且理由妥当（相关物品的决定权会在回报玩家身上，但是处罚决定权会由伺服管理员进行）\n" +
            "&f&l9. 规则随时可以添加并且更改（伺服管理员拥有最终决定权）"
        ).toComponent())
    }
}