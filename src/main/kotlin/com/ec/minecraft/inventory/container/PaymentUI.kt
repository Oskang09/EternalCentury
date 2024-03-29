package com.ec.minecraft.inventory.container

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.UIProvider
import com.ec.util.StringUtil.toComponent
import dev.reactant.resquare.dom.childrenOf
import dev.reactant.resquare.dom.declareComponent
import dev.reactant.resquare.dom.unaryPlus
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import dev.reactant.resquare.render.useCancelRawEvent
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PaymentUI: UIProvider<PaymentUI.PaymentUIProps>("payment") {

    data class PaymentUIProps(
        val topupMapper: MutableMap<Int, Int> = mutableMapOf(),
        val displayMapper: List<ItemStack> = listOf(),
    )

    private val styles = object {

        val container = styleOf {
            width = 100.percent
            height = 100.percent
            padding(1.px)
            flexWrap.wrap()
            alignContent.flexStart()
        }

        val item = styleOf {
            width = 1.px
            height = 1.px
            flexShrink = 0f
        }

    }

    override val isStaticProps: Boolean = true
    override fun info(props: PaymentUIProps): UIBase {
        return UIBase(
            rows = 3,
            cols = 9,
            title = "&b[&5系统&b] &6伺服赞助"
        )
    }

    override fun props(player: HumanEntity): PaymentUIProps {
        return PaymentUIProps(
            topupMapper = mutableMapOf(
                170 to 5,
                600 to 40,
                1100 to 100,
                3300 to 320,
                5250 to 550,
                10000 to 1400,
                20000 to 3000
            ),
            displayMapper = listOf(
                globalManager.component.item(Material.GRASS_BLOCK) {
                     it.displayName("&f[&e赞助&f] &f草地点卷".toComponent())
                     it.lore(arrayListOf(
                         "&7需付费 &f- &eMYR 1.70",
                         "&7点数获得 &f- &e5",
                         "",
                         "&f&l付费前必须知道",
                         "&f1. 请理性消费",
                         "&f2. 只有适当的理由才能进行退款",
                         "&f3. 超过一定的时间是无法退款的",
                         "&f4. 理由充分服主有权利手动退款"
                     ).toComponent())
                },
                globalManager.component.item(Material.STONE) {
                    it.displayName("&f[&e赞助&f] &f石头点卷".toComponent())
                    it.lore(arrayListOf(
                        "&7需付费 &f- &eMYR 6.00",
                        "&7获得点数 &f- &e40",
                        "",
                        "&f&l付费前必须知道",
                        "&f1. 请理性消费",
                        "&f2. 只有适当的理由才能进行退款",
                        "&f3. 超过一定的时间是无法退款的",
                        "&f4. 理由充分服主有权利手动退款"
                    ).toComponent())
                },
                globalManager.component.item(Material.IRON_INGOT) {
                    it.displayName("&f[&e赞助&f] &f铁块点卷".toComponent())
                    it.lore(arrayListOf(
                        "&7需付费 &f- &eMYR 11",
                        "&7点数获得 &f- &e100",
                        "",
                        "&f&l付费前必须知道",
                        "&f1. 请理性消费",
                        "&f2. 只有适当的理由才能进行退款",
                        "&f3. 超过一定的时间是无法退款的",
                        "&f4. 理由充分服主有权利手动退款"
                    ).toComponent())
                },
                globalManager.component.item(Material.GOLD_INGOT) {
                    it.displayName("&f[&e赞助&f] &f金块点卷".toComponent())
                    it.lore(arrayListOf(
                        "&7需付费 &f- &eMYR 33",
                        "&7点数获得 &f- &e320",
                        "",
                        "&f&l付费前必须知道",
                        "&f1. 请理性消费",
                        "&f2. 只有适当的理由才能进行退款",
                        "&f3. 超过一定的时间是无法退款的",
                        "&f4. 理由充分服主有权利手动退款"
                    ).toComponent())
                },
                globalManager.component.item(Material.DIAMOND) {
                    it.displayName("&f[&e赞助&f] &f钻石点卷".toComponent())
                    it.lore(arrayListOf(
                        "&7需付费 &f- &eMYR 52.50",
                        "&7点数获得 &f- &e550",
                        "",
                        "&f&l付费前必须知道",
                        "&f1. 请理性消费",
                        "&f2. 只有适当的理由才能进行退款",
                        "&f3. 超过一定的时间是无法退款的",
                        "&f4. 理由充分服主有权利手动退款"
                    ).toComponent())
                },
                globalManager.component.item(Material.EMERALD) {
                    it.displayName("&f[&e赞助&f] &f翡翠点卷".toComponent())
                    it.lore(arrayListOf(
                        "&7需付费 &f- &eMYR 100",
                        "&7点数获得 &f- &e1400",
                        "",
                        "&f&l付费前必须知道",
                        "&f1. 请理性消费",
                        "&f2. 只有适当的理由才能进行退款",
                        "&f3. 超过一定的时间是无法退款的",
                        "&f4. 理由充分服主有权利手动退款"
                    ).toComponent())
                },
                globalManager.component.item(Material.NETHERITE_INGOT) {
                    it.displayName("&f[&e赞助&f] &f地狱点卷".toComponent())
                    it.lore(arrayListOf(
                        "&7需付费 &f- &eMYR 200",
                        "&7点数获得 &f- &e3000",
                        "",
                        "&f&l付费前必须知道",
                        "&f1. 请理性消费",
                        "&f2. 只有适当的理由才能进行退款",
                        "&f3. 超过一定的时间是无法退款的",
                        "&f4. 理由充分服主有权利手动退款"
                    ).toComponent())
                },
            )
        )
    }

    override val render = declareComponent<PaymentUIProps> { props ->
        useCancelRawEvent()

        div(DivProps(
            style = styles.container,
            item = ItemStack(Material.WHITE_STAINED_GLASS_PANE),
            children = childrenOf(
                +(props.topupMapper.toList().mapIndexed { count,pair ->
                    val item = props.displayMapper[count]
                    return@mapIndexed div(DivProps(
                        style = styles.item,
                        item = item,
                        onClick = { event ->
                            val typedPlayer = event.whoClicked as Player

                            typedPlayer.closeInventory()
                            globalManager.payments.generatePaymentURL(
                                typedPlayer, pair.first,
                                "购买游戏点卷 ${pair.second}",
                                "payment made to eternal century"
                            )
                        }
                    ))
                })
            )
        ))
    }
}