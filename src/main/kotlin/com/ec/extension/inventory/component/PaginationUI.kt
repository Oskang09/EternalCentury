package com.ec.extension.inventory.component

import com.ec.extension.inventory.UIProvider
import com.ec.model.Observable
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
import org.bukkit.inventory.ItemStack

class PaginationUIProps(
    val info: ItemStack = ItemStack(Material.AIR),
    val items: List<PaginationItem> = mutableListOf(),
    val itemsPerPage: Int = 42
)

class PaginationItem(
    val item: ItemStack = ItemStack(Material.AIR),
    val click: EventHandler<ResquareClickEvent>? = null,
)

abstract class PaginationUI(val name: String): UIProvider<PaginationUIProps>(name) {

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

    private val refresher = Observable<Boolean>()

    protected fun refresh() {
        refresher.onNext(true)
    }

    override val render = declareComponent<PaginationUIProps> { props ->
        useCancelRawEvent()

        val (page, setPage) = useState(0)
        val renderItems = props.items.drop(page * props.itemsPerPage).take(props.itemsPerPage)
        var isFirst = page == 0
        val isLast = props.items.size / props.itemsPerPage < page + 1
        refresher.subscribeOnce { setPage(page) }

        div(DivProps(
            style = styles.container,
            children = childrenOf(
                div(DivProps(
                    style = styles.leftBar,
                    children = childrenOf(
                        div(DivProps(
                            item = props.info,
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
                        +(if (isLast) null else div(DivProps(
                            style = styleOf(styles.leftBarItem){
                               marginTop = 3.px
                            },
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
                        +(renderItems.map {
                            return@map div(DivProps(
                                style = styles.item,
                                item = it.item,
                                onClick = it.click
                            ))
                        })
                    )
                ))
            )
        ))
    }

}