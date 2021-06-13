package com.ec.extension.store

import com.ec.config.StoreConfig
import com.ec.config.StoreConfigItem
import com.ec.extension.GlobalManager
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.resquare.bukkit.container.createUI
import dev.reactant.resquare.dom.childrenOf
import dev.reactant.resquare.dom.declareComponent
import dev.reactant.resquare.dom.unaryPlus
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import dev.reactant.resquare.event.EventHandler
import dev.reactant.resquare.event.ResquareClickEvent
import dev.reactant.resquare.render.useCancelRawEvent
import dev.reactant.resquare.render.useState
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.exposed.sql.transactions.transaction

@Component
class StoreManager {

    data class StoreUIProps(
        val owner: String?,
        val config: StoreConfig,
    )

    private lateinit var globalManager: GlobalManager

    fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

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

        val itemContainer = styleOf {
            width = 7.px
            height = 9.px
            flexWrap.wrap()
            alignContent.flexStart()
        }

        val item = styleOf {
            width = 1.px
            height = 1.px
            flexShrink = 0f
        }

    }

    private val render = declareComponent<StoreUIProps> { props ->
        useCancelRawEvent()

        val (state, setState) = useState("PURCHASE")
        val (page, setPage) = useState(0)
        val items = props.config.purchasing.drop(page * 42).take(42)
        var isFirst = page == 0
        val isLast = items.size / 42 < page + 1

        val eventHandler: (StoreConfigItem) -> EventHandler<ResquareClickEvent> = {
            { event ->
                val player = event.whoClicked as Player
                var price = it.price
                if (event.isShiftClick) {
                    price = it.totalPrice
                }

                val enoughMoney = globalManager.economy.has(player, price)
                if (enoughMoney) {
                    globalManager.economy.withdrawPlayer(player, price)
                    if (props.owner == null) {

                    } else {
                        globalManager.economy.depositPlayer(props.owner, price)
                        transaction {

                        }
                    }
                }
            }
        }

        div(DivProps(
            style = styles.container,
            children = childrenOf(
                div(DivProps(
                    style = styles.leftBar,
                    children = childrenOf(
                        div(DivProps(
                            item = if (props.owner != null) globalManager.component.playerHead(props.owner) {
                                it.setDisplayName("&f[&5系统&f] &a玩家商店")
                            } else globalManager.component.item(Material.BOOK) {
                                it.setDisplayName("&f[&5系统&f] &a伺服商店")
                            },
                            style = styles.leftBarItem
                        )),
                        div(DivProps(
                            style = styles.leftBarItem,
                            item = ItemStack(Material.BLACK_STAINED_GLASS_PANE),
                        )),
                        div(DivProps(
                            style = styles.leftBarItem,
                            item = globalManager.component.item(Material.CHEST, glowing = state == "SELL") {
                                it.setDisplayName("&5显示 -> &f&l可售物品")
                            },
                            onClick = {
                                setState("PURCHASE")
                                setPage(1)
                            }
                        )),
                        div(DivProps(
                            style = styles.leftBarItem,
                            item = globalManager.component.item(Material.ITEM_FRAME, glowing = state == "PURCHASE") {
                                it.setDisplayName("&5显示 -> &f&l可购物品")
                            },
                            onClick = {
                                setState("SELL")
                                setPage(1)
                            }
                        )),
                        +(if (isFirst)  null else div(DivProps(
                            style = styles.leftBarItem,
                            item = globalManager.component.arrowPrevious(),
                            onClick = {
                                setPage(page - 1)
                            }
                        ))),
                        +(if (isLast) null else div(DivProps(
                            style = styles.leftBarItem,
                            item = globalManager.component.arrowNext(),
                            onClick = {
                                setPage(page + 1)
                            }
                        )))
                    )
                )),
                div(DivProps(
                    style = styles.verticalBar,
                    item = ItemStack(Material.WHITE_STAINED_GLASS_PANE),
                )),
                div(DivProps(
                    style = styles.itemContainer,
                    children = childrenOf(
                        +(items.map {
                            it.item.itemMeta<ItemMeta> {
                                val lores = lore ?: mutableListOf()
                                lores.add("")
                                lores.add(" &6--- &5系统叙述 &6---".colorize())

                                var previewText = "库存"
                                if (state == "SELL") {
                                    previewText = "还需"
                                }
                                lores.add("&f$previewText &f： &e&l${it.amount}")
                                lore = lores
                            }
                            return@map div(DivProps(
                                style = styles.item,
                                item = it.item,
                                onClick = eventHandler(it)
                            ))
                        })
                    )
                ))
            )
        ))
    }

    fun displayTo(props: StoreUIProps, target: Player) {
        val container = createUI(
            render, props, 9, 6, props.config.display.colorize(),
            multiThreadComponentRender = true,
            multiThreadStyleRender = true,
            autoDestroy = true
        )

        container.openInventory(target)
    }
}