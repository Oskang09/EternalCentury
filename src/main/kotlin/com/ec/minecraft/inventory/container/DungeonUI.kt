package com.ec.minecraft.inventory.container

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.model.player.ECPlayerGameState
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta

class DungeonUI: PaginationUI<Unit>("dungeon") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6副本咨询"
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        return PaginationUIProps(
            info = globalManager.component.playerHead(player as Player) {
                it.displayName("&b[&5系统&b] &6副本咨询".toComponent())
            },
            extras = extras@{ state ->
                val ecPlayer = globalManager.players.getByPlayer(player)
                if (ecPlayer.gameState == ECPlayerGameState.ARENA) {
                    val arena = globalManager.arenas.getArenaById(ecPlayer.gameName)
                    return@extras listOf(
                        div(DivProps(
                            style = styleOf {
                                width = 1.px
                                height = 1.px
                            },
                            item = globalManager.items.getItem(arena.config.info),
                            onClick = {
                                globalManager.inventory.displayPlayerDungeon(it.whoClicked as Player)
                            }
                        )),
                    )
                }

                val mode = state.getStateByKeyOrDefault("mode", "LIST")
                listOf(
                    div(DivProps(
                        style = styleOf {
                            width = 1.px
                            height = 1.px
                        },
                        onClick = {
                            if (mode == "CREATE") {
                                state.setStateByKey("mode", "LIST")
                            } else {
                                state.setStateByKey("mode", "CREATE")
                            }

                            state.refreshState()
                        },
                        item = globalManager.component.item(Material.SLIME_BALL) {
                            it.displayName("&f[&5系统&f] &a选择并开启副本".toComponent())
                        }
                    )),
                )
            },
            items = {
                val mode = it.getStateByKeyOrDefault("mode", "LIST")
                if (mode == "LIST") {
                    globalManager.arenas.getArenas()
                        .map { arena ->
                            val display = globalManager.items.getItem(arena.config.info)
                            display.itemMeta<ItemMeta> {
                                val lores = lore() ?: mutableListOf()
                                lores.add("".toComponent())
                                lores.add("&f副本 - &e${arena.config.name}".toComponent())
                                lores.add("&f房主 - &r".toComponent().append(arena.host.displayName()))
                                lores.add("&f玩家 - &f${arena.players.size}".toComponent())

                                lore(lores)
                            }

                            return@map PaginationItem(
                                item = display,
                                click = { evt ->
                                    arena.onJoin(evt.whoClicked as Player)
                                }
                            )
                        }
                } else {
                    globalManager.arenas.getArenaConfigs()
                        .map { arena ->
                            val display = globalManager.items.getItem(arena.info)
                            return@map PaginationItem(
                                item = display,
                                click = { evt ->
                                    globalManager.arenas.createArena(evt.whoClicked as Player, arena.id)
                                    it.refreshState()
                                }
                            )
                        }
                }
            },
        )
    }

}