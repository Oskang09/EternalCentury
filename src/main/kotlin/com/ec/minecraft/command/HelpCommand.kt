package com.ec.minecraft.command

import com.ec.manager.GlobalManager
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "echelp",
    description = ["查看可用指令列表"]
)
internal class HelpCommand(private val globalManager: GlobalManager): ReactantCommand() {

    @CommandLine.Parameters(
        arity = "0..1",
        paramLabel = "玩家名称",
        description = ["你要收到/无视的玩家名称"],
    )
    var commandType: String? = null

    private val commands = mapOf(
        "mcmmo" to listOf(
            "/mctop       - 查看总排行榜",
            "/mcrank      - 查看自己的技能排行",
            "/mcstats     - 查看自己的技能咨询",
            "/mcability   - 开/关技能启动（防止一些插件冲突）",
            "/acrobatics  - 杂技技能咨询",
            "/archery     - 箭术技能咨询",
            "/axes        - 斧技技能咨询",
            "/excavation  - 挖掘技能咨询",
            "/fishing     - 钓鱼技能咨询",
            "/herbalism   - 草药学技能咨询",
            "/mining      - 挖矿技能咨询",
            "/repair      - 修理技能咨询",
            "/swords      - 剑术技能咨询",
            "/taming      - 驯兽技能咨询",
            "/unarmed     - 格斗技能咨询",
            "/woodcutting - 伐木技能咨询"
        ),
        "party" to listOf(
            "/party help   - 查看队伍指令列表",
            "/party        - 查看您的队伍咨询",
        ),
        "res" to listOf(
            "/res limits - 查看您的领地限制",
            "/res help   - 查看领地指令列表",
            "",
            "1. 用 \"线\" 来查看领地是否其他玩家已经圈了",
            "2. 用 \"木锄\" 来进行圈地"
        ),
        "chat" to listOf(
            "@party <讯息>       - 发送队伍讯息",
            "[ item ]           - 展示手上的物品",
            "[ inv ]            - 展示您的当前背包",
            "[ ender ]          - 展示末影箱子",
            "[ money ]          - 展示您的金钱",
            "[ ping ]           - 展示您的延迟",
        ),
        "basic" to listOf(
            "/balance         - 查看金钱",
            "/ignore <玩家>    - 无视玩家的所有请求",
            "/pm <玩家> <讯息>  - 私聊玩家",
            "/sell <价格>      - 贩卖物品到拍卖商场",
            "/tpa <玩家>       - 接受玩家的传送请求",
            "/tpb <玩家>       - 禁止玩家向你发送传送请求",
            "/tp <玩家>        - 向玩家发送传送请求"
        )
    )

    override fun execute() {
        if (commandType == null || commands[commandType] == null) {
            sender.sendMessage("&f/echelp       - 打开此指令列表".colorize())
            sender.sendMessage("&f/echelp mcmmo - 打开MCMMO指令列表".colorize())
            sender.sendMessage("&f/echelp party - 打开队伍指令列表".colorize())
            sender.sendMessage("&f/echelp res   - 打开领地指令列表".colorize())
            sender.sendMessage("&f/echelp chat  - 打开聊天教学".colorize())
            sender.sendMessage("&f/echelp basic - 打开基本指令列表".colorize())
            return
        }

        sender.sendMessage(commands[commandType]!!.map { "&f$it".colorize() }.toTypedArray())
    }

}