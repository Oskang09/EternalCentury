package com.ec.extension.inventory.component

import com.ec.extension.inventory.UIProvider
import com.ec.model.Observable
import com.ec.util.QueryUtil
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
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.ResultRow

class IteratorUIProps(
    val info: ItemStack = ItemStack(Material.AIR),
    val itemsGetter: (String) -> QueryUtil.IteratorResult,
    val itemMapper: (ResultRow) -> IteratorItem,
    val extras: List<Node?> = listOf(),
)

class IteratorItem(
    val item: ItemStack = ItemStack(Material.AIR),
    val click: EventHandler<ResquareClickEvent>? = null,
)

abstract class IteratorUI<T>(val name: String): UIProvider<IteratorUIProps>(name) {

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

    open fun props(player: HumanEntity, props: T?): IteratorUIProps {
        return props(player)
    }

    fun displayWithProps(player: HumanEntity, props: T) {
        this.displayTo(player, props(player, props))
    }

    override val render = declareComponent<IteratorUIProps> { props ->
        useCancelRawEvent()

        val (page, setPage) = useState(0)
        val (previousCursor, setPreviousCursor) = useState("")
        val (nextCursor, setNextCursor) = useState("")
        val (items, setItems) = useState<MutableMap<Int, List<ResultRow>>>(mutableMapOf())

        var isFirst = page == 0
        val isLast = nextCursor == "" && page == items.size - 1

        val extras = props.extras.filterNotNull()
        val extraLength = extras.size + if (isFirst) 0 else 1

        useEffect({
            if (items[page] == null) {
                val iterator = props.itemsGetter(nextCursor)
                items[page] = iterator.items
                setPreviousCursor(nextCursor)
                setNextCursor(iterator.cursor)
                setItems(items)
            }
            return@useEffect { }
        }, arrayOf(page))

        refresher.subscribeOnce {
            val iterator = props.itemsGetter(previousCursor)
            items[page] = iterator.items
            setNextCursor(iterator.cursor)
            setItems(items)
        }

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
                        +(if (isFirst) div(DivProps(styles.leftBarItem, globalManager.component.item(Material.AIR) )) else div(DivProps(
                            style = styles.leftBarItem,
                            item = globalManager.component.arrowPrevious(),
                            onClick = {
                                setPage(page - 1)
                            }
                        ))),
                        +extras,
                        +(if (isLast) null else div(DivProps(
                            style = styleOf(styles.leftBarItem) {
                                marginTop = (3-extraLength).px
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
                        +(items[page]?.map { result ->
                            val it = props.itemMapper(result)
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