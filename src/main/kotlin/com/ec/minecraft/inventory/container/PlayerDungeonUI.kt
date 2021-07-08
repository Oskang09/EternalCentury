package com.ec.minecraft.inventory.container

import com.ec.manager.arena.IArena
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

class PlayerDungeonUI: UIProvider<PlayerDungeonUI.PlayerDungeonUIProps>("player-dungeon") {

    data class PlayerDungeonUIProps(
        val host: Player,
        val arena: IArena,
        val members: List<ItemStack>,
    )
    private val styles = object {

        val container = styleOf {
            width = 9.px
            height = 6.px
        }

        val leftBar = styleOf {
            width = 2.px
            height = 2.px
            flexWrap.wrap()
            alignContent.flexStart()
        }

        val rightBar = styleOf {
            width = 6.px
            height = 2.px
            flexWrap.wrap()
            alignContent.flexStart()
        }

        val display = styleOf {
            width = 1.px
            height = 1.px
        }
    }

    override fun info(props: PlayerDungeonUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6副本组队",
            rows = 2
        )
    }

    override fun props(entity: HumanEntity): PlayerDungeonUIProps {
        val player = entity as Player
        val ecPlayer = globalManager.players.getByPlayer(player)
        val arena = globalManager.arenas.getArenaById(ecPlayer.gameName)
        return PlayerDungeonUIProps(
            arena.host,
            arena,
            listOf(
                globalManager.component.playerHead(arena.host),
                *arena.players.map {
                    if (it.name == arena.host.name) return@map null
                    globalManager.component.playerHead(it)
                }.filterNotNull().toTypedArray()
            ),
        )
    }

    override val render = declareComponent<PlayerDungeonUIProps> { props ->

        useCancelRawEvent()

        div(DivProps(
            style = styles.container,
            children = childrenOf(
                div(DivProps(
                    style = styles.leftBar,
                    children = childrenOf(
                        div(DivProps(
                            style = styles.display,
                            item = globalManager.items.getItem(props.arena.config.info)
                        )),
                        div(DivProps(
                            style = styles.display,
                            item = ItemStack(Material.WHITE_STAINED_GLASS_PANE)
                        )),
                        div(DivProps(
                            style = styles.display,
                            item = globalManager.component.item(Material.SLIME_BALL) {
                                it.displayName("&f[&5系统&f] &a开启副本".toComponent())
                            },
                            onClick = {
                                props.arena.onStart()
                            }
                        )),
                        div(DivProps(
                            style = styles.display,
                            item = ItemStack(Material.WHITE_STAINED_GLASS_PANE)
                        )),
                    )
                )),
                div(DivProps(
                    style = styles.rightBar,
                    children = childrenOf(
                        +(props.members.map {
                            return@map div(DivProps(
                                style = styles.display,
                                item = it,
                            ))
                        })
                    )
                ))
            )
        ))
    }
}