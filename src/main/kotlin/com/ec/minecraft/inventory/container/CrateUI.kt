package com.ec.minecraft.inventory.container

import com.ec.config.CrateConfig
import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.UIProvider
import com.ec.extension.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import dev.reactant.resquare.dom.Node
import dev.reactant.resquare.dom.childrenOf
import dev.reactant.resquare.dom.declareComponent
import dev.reactant.resquare.dom.unaryPlus
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import dev.reactant.resquare.render.useCancelRawEvent
import dev.reactant.resquare.render.useState
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

class CrateUI: UIProvider<CrateUI.CrateUIProps>("crate") {

    data class CrateUIProps(
        val crates: List<CrateConfig> = listOf()
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

    override fun info(props: CrateUIProps): UIBase {
        return UIBase(
            rows = 6,
            cols = 9,
            title = "&b[&5系统&b] &6抽奖水池".colorize()
        )
    }

    override fun props(player: HumanEntity): CrateUIProps {
        return CrateUIProps(globalManager.crates.getCrates())
    }

    override val render = declareComponent<CrateUIProps> { props ->
        useCancelRawEvent()

        val (page, setPage) = useState(0)
        val (crate, setCrate) = useState<CrateConfig?>(null)

        var info: ItemStack
        var items: List<Node>
        if (crate != null) {
            info = globalManager.items.getItemByConfig(crate.display)
            items = crate.rewards.map {
                div(DivProps(
                    style = styles.item,
                    item = globalManager.items.getItem(it.item),
                ))
            }
        } else {
            info = globalManager.component.item(Material.CHEST) {
                it.setDisplayName("&f[&5系统&f] &f抽奖水池".colorize())
                it.lore = arrayListOf("&7抽奖宝箱 &f- &a${props.crates.size}").colorize()
            }
            items = props.crates.map {
                div(DivProps(
                    style = styles.item,
                    item = globalManager.items.getItemByConfig(it.display),
                    onClick = { _ ->
                        setPage(1)
                        setCrate(it)
                    }
                ))
            }
        }

        val renderItems = items.drop(page * 42).take(42)
        var isFirst = page == 0
        val isLast = items.size / 42 < page + 1

        div(DivProps(
            style = styles.container,
            children = childrenOf(
                div(DivProps(
                    style = styles.leftBar,
                    children = childrenOf(
                        div(DivProps(
                            item = info,
                            style = styles.leftBarItem
                        )),
                        div(DivProps(
                            style = styles.leftBarItem,
                            item = ItemStack(Material.BLACK_STAINED_GLASS_PANE),
                        )),
                        +(if (isFirst)  null else div(DivProps(
                            style = styles.leftBarItem,
                            item = globalManager.component.arrowPrevious(),
                            onClick = {
                                setPage(page - 1)
                            }
                        ))),
                        +(if (crate == null) null else div(DivProps(
                            style = styles.leftBarItem,
                            item = globalManager.component.item(Material.BARRIER) {
                                it.setDisplayName("&b[&5系统&b] &6返回".colorize())
                            },
                            onClick = {
                                setPage(0)
                                setCrate(null)
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
                    children = childrenOf(+renderItems)
                ))
            )
        ))
    }

}