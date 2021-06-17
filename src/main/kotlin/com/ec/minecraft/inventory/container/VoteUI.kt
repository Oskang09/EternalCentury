package com.ec.minecraft.inventory.container

import com.ec.database.Players
import com.ec.database.VoteRewards
import com.ec.database.Votes
import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.UIProvider
import com.ec.model.player.ECPlayer
import com.ec.util.StringUtil.colorize
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.resquare.dom.Node
import dev.reactant.resquare.dom.childrenOf
import dev.reactant.resquare.dom.declareComponent
import dev.reactant.resquare.dom.unaryPlus
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import dev.reactant.resquare.event.EventHandler
import dev.reactant.resquare.event.ResquareClickEvent
import dev.reactant.resquare.render.useCancelRawEvent
import dev.reactant.resquare.render.useEffect
import dev.reactant.resquare.render.useState
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class VoteUI: UIProvider<VoteUI.VoteUIProps>("vote") {

    private val internalRewardDateMapper = mapOf(
        listOf(1,2,3,4,5) to 1,
        listOf(6,7,8,9,10) to 2,
        listOf(11,12,13,14,15) to 3,
        listOf(16,17,18,19,20) to 4,
        listOf(16,17,18,19,25) to 5,
        listOf(26,27,28,29,30) to 6,
    )

    private val if28Days = listOf(26,27,28)
    private val if29Days = listOf(26,27,28,29)
    private val if31Days = listOf(31)

    data class VoteUIProps(
        val player: ECPlayer,
        val currentDay: Int,
        val currentMonth: Int,
        val currentYear: Int,
        val numOfDays: Int
    )

    private val styles = object {

        val container = styleOf {
            width = 9.px
            height = 6.px
        }

        val leftBar = styleOf {
            width = 1.px
            height = 6.px
            flexDirection.column()
        }

        val leftBarItem = styleOf {
            width = 1.px
            height = 1.px
        }

        val verticalBar = styleOf {
            height = 6.px
            width = 1.px
        }

        val displayContainer = styleOf {
            width = 7.px
            height = 6.px
            flexWrap.wrap()
            alignContent.flexStart()
        }

        val itemContainer = styleOf {
            width = 7.px
            height = 5.px
            flexWrap.wrap()
            alignContent.flexStart()
            flexDirection.column()
        }

        val item = styleOf {
            width = 1.px
            height = 1.px
            flexShrink = 0f
        }
    }

    override fun info(props: VoteUIProps): UIBase {
        return UIBase(
            title = ("&b[&5系统&b] &f每日签到").colorize(),
            rows = 6,
            cols = 9
        )

    }

    override fun props(player: HumanEntity): VoteUIProps {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"))
        val numOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        return VoteUIProps(ecPlayer, currentDay, currentMonth, currentYear, numOfDays)
    }


    override val render = declareComponent<VoteUIProps> { props ->
        useCancelRawEvent()

        val (signedDays, setSignedDays) = useState<Array<Int>?>(null)
        val (eligibleRewards, setEligibleRewards) = useState<Array<Int>?>(null)
        val (claimedRewards, setClaimedRewards) = useState<Array<Int>?>(null)

        useEffect({
            transaction {
                val days = Votes.select { Votes.year eq props.currentYear }
                    .andWhere { Votes.month eq props.currentMonth }
                    .andWhere { Votes.playerId eq props.player.database[Players.id] }
                    .map { return@map it[Votes.day] }
                    .toMutableList()

                val doneRewards = VoteRewards.select { VoteRewards.year eq props.currentYear }
                    .andWhere { VoteRewards.month eq props.currentMonth }
                    .andWhere { VoteRewards.playerId eq props.player.database[Players.id] }
                    .map { return@map it[VoteRewards.reward] }
                    .toMutableList()

                val rewards = mutableListOf<Int>()
                internalRewardDateMapper.forEach { (dates, reward) ->
                    if (days.containsAll(dates)) {
                        rewards.add(reward)
                    }
                }

                when (props.numOfDays) {
                    28 -> {
                        if (days.containsAll(if28Days)) {
                            rewards.add(6)
                        }
                    }
                    29 -> {
                        if (days.containsAll(if29Days)) {
                            rewards.add(6)
                        }
                    }
                    31 -> {
                        if (days.containsAll(if31Days)) {
                            rewards.add(7)
                        }
                    }
                }

                setClaimedRewards(doneRewards.toTypedArray())
                setEligibleRewards(rewards.toTypedArray())
                setSignedDays(days.toTypedArray())
            }

            return@useEffect {}
        }, arrayOf())

        if (signedDays == null || eligibleRewards == null || claimedRewards == null) {
            return@declareComponent div()
        }

        val renderSign = mutableListOf<Node>()
        repeat(props.numOfDays) {
            val day = it + 1
            val isSigned = signedDays.contains(day)
            val previewText = if (isSigned) "已签到" else "未签到"

            renderSign.add(div(DivProps(
                style = styles.item,
                item = globalManager.component.item(if (isSigned) Material.LIME_WOOL else Material.RED_WOOL) { meta ->
                    meta.setDisplayName("&b[&5$previewText&b] &l${props.currentYear}-${props.currentMonth}-${day}".colorize())
                    val lores = arrayListOf(
                        "&7 - &e100 金钱",
                        "&7 - &e一个附魔之瓶"
                    )

                    val key = "${props.currentYear}-${props.currentMonth}-${day}"
                    val extraRewards = globalManager.serverConfig.signRewards[key]
                    extraRewards?.forEach { cfg ->
                        lores.add(cfg.display)
                    }

                    meta.lore = lores.colorize()
                },
            )))
        }

        val renderRewards = mutableListOf<Node>()
        repeat(if (props.numOfDays == 31) 7 else 6) { index ->
            val rewardIndex = index + 1

            var isEligible = false
            var isClaimed = false
            if (eligibleRewards.contains(rewardIndex)) {
                isEligible = true
            }

            if (claimedRewards.contains(rewardIndex)) {
                isClaimed = true
            }

            var previewText = "未完成"
            if (isEligible && isClaimed) {
                previewText = "已领取"
            }

            if (isEligible && !isClaimed) {
                previewText = "已完成"
            }

            var eventHandler: EventHandler<ResquareClickEvent>? = null
            if (isEligible && !isClaimed) {
                eventHandler = {
                    val player = props.player.player

                    if (props.player.ensureUpdate("daily vote streak item", isAsync = true) {
                            VoteRewards.insert {
                                it[id] = "".generateUniqueID()
                                it[playerId] = props.player.database[Players.id]
                                it[year] = props.currentYear
                                it[month] = props.currentMonth
                                it[reward] = rewardIndex
                                it[signedAt] = Instant.now().epochSecond
                            }
                        }) {

                        val key = "${props.currentYear}-${props.currentMonth}-streak-${rewardIndex}"
                        val extraRewards = globalManager.serverConfig.signRewards[key]
                        if (extraRewards != null) {
                            globalManager.sendRewardToPlayer(player, extraRewards)
                        }

                        player.inventory.addItem(globalManager.enchantments.getRandomEnchantedBook(1, 3))
                        player.inventory.addItem(ItemStack(globalManager.items.getRandomEggMaterial(), 1))
                        globalManager.economy.depositPlayer(player, 500.0)
                        player.sendMessage(globalManager.message.system("连续签到奖励领取成功！"))
                    }

                    setClaimedRewards(arrayOf(*claimedRewards, rewardIndex))
                }
            }

            renderRewards.add(div(DivProps(
                style = styles.item,
                onClick = eventHandler,
                item = globalManager.component.item(Material.CHEST) { meta ->
                    meta.setDisplayName("&b[&5$previewText&b] &f&l连续签到奖励 $rewardIndex".colorize())
                    val lores = arrayListOf(
                        "&7 - &e500 金钱",
                        "&7 - &e随机一本附魔书",
                        "&7 - &e随机一个生物蛋"
                    )

                    val key = "${props.currentYear}-${props.currentMonth}-streak-${rewardIndex}"
                    val extraRewards = globalManager.serverConfig.signRewards[key]
                    extraRewards?.forEach { cfg ->
                        lores.add(cfg.display)
                    }

                    meta.lore = lores.colorize()
                },
            )))
        }

        val isTodaySigned = signedDays.contains(props.currentDay)
        var todayEventHandler: EventHandler<ResquareClickEvent>? = null
        if (!isTodaySigned) {
             todayEventHandler = {
                 val player = props.player.player

                 if (props.player.ensureUpdate("daily vote", isAsync = true) {
                         Votes.insert {
                             it[id] = "".generateUniqueID()
                             it[playerId] = props.player.database[Players.id]
                             it[year] = props.currentYear
                             it[month] = props.currentMonth
                             it[day] = props.currentDay
                             it[signedAt] = Instant.now().epochSecond
                         }
                     }) {
                     val key = "${props.currentYear}-${props.currentMonth}-${props.currentDay}"
                     val extraRewards = globalManager.serverConfig.signRewards[key]
                     if (extraRewards != null) {
                         globalManager.sendRewardToPlayer(player, extraRewards)
                     }

                     player.inventory.addItem(ItemStack(Material.EXPERIENCE_BOTTLE, 1))
                     globalManager.economy.depositPlayer(player, 100.0)
                     props.player.player.sendMessage(globalManager.message.system("签到成功！"))
                 }

                 setSignedDays(arrayOf(*signedDays, props.currentDay))
             }
        }

        div(DivProps(
            style = styles.container,
            children = childrenOf(
                div(DivProps(
                    style = styles.leftBar,
                    children = childrenOf(
                        div(DivProps(
                            item =  globalManager.component.playerHead(props.player.player) {
                                it.setDisplayName("&b[&5系统&b] &6每日签到".colorize())
                                it.lore = arrayListOf(
                                    "&7已签到数 &f- &a${signedDays.size}",
                                    "&7总签到数 &f-  &a${props.numOfDays}"
                                ).colorize()
                            },
                            style = styles.leftBarItem
                        )),
                        div(DivProps(
                            style = styleOf(styles.leftBarItem){
                                marginTop = 4.px
                            },
                            item = globalManager.component.item(if (isTodaySigned) Material.BOOK else Material.WRITABLE_BOOK) {
                                var previewText = "未完成"
                                if (isTodaySigned) {
                                    previewText = "已完成"
                                }
                                it.setDisplayName("&b[&5$previewText&b] &6今日签到".colorize())
                            },
                            onClick = todayEventHandler
                        )),
                    )
                )),
                div(DivProps(
                    style = styles.verticalBar,
                    item = ItemStack(Material.WHITE_STAINED_GLASS_PANE),
                )),
                div(DivProps(
                    style = styles.displayContainer,
                    children = childrenOf(
                        div(DivProps(
                            style = styles.itemContainer,
                            children = childrenOf(+renderSign)
                        )),
                        div(DivProps(
                            children = childrenOf(+renderRewards)
                        ))
                    )
                ))
            )
        )
        )
    }
}