package com.ec.minecraft.inventory

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.PaginationItem
import com.ec.extension.inventory.component.PaginationUI
import com.ec.extension.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import dev.reactant.resquare.dom.childrenOf
import dev.reactant.resquare.dom.declareComponent
import dev.reactant.resquare.dom.unaryPlus
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import dev.reactant.resquare.render.useCancelRawEvent
import dev.reactant.resquare.render.useState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.IllegalStateException


class TitleUI: PaginationUI("title") {

    data class TitleUIProps(
        val items: List<ItemStack>,
        val unlockedTitle: Int,
        val totalTitle: Int,
    )

    private val styles = object {

        val verticalBar = styleOf {
            marginLeft = 1.px
            height = 100.percent
        }

        val separator = styleOf {
            marginTop = 1.px
        }

        val arrowUp = styleOf {
            marginTop = 2.px
        }

        val arrowDown = styleOf {
            marginTop = 5.px
        }

        val itemContainer = styleOf {
            marginLeft = 2.px
            width = 100.percent
            height = 100.percent
            flexWrap.wrap()
            alignContent.flexStart()
        }

        val item = styleOf {
            width = 1.px
            height = 1.px
            flexShrink = 0f
        }

    }

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6称号列表".colorize()
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        val ecPlayer = globalManager.players.getByPlayer(player as Player)!!
        val playerTitles = ecPlayer.data.availableTitles
        val titles = globalManager.titles.getTitles()
        val views = titles.sortedBy { it.position }
            .map {
                if (playerTitles.contains(it.id)) {
                    var display = it.getItemStack(ItemStack(Material.NAME_TAG))
                    if (ecPlayer.data.currentTitle == it.id) {
                        display = globalManager.component.withGlow(display) { meta ->
                            meta.lore?.add("")
                            meta.lore?.add(" &6--- &1目前称号使用中 &6---".colorize())
                        }
                    }

                    return@map PaginationItem(display) { _ ->
                        player.closeInventory()

                        ecPlayer.ensureUpdate("update title to ${it.id}") { config ->
                            config.content.currentTitle = it.id
                        }
                    }
                } else {
                    return@map PaginationItem(
                        item =  it.getItemStack(ItemStack(Material.BARRIER))
                    )
                }
            }

        return PaginationUIProps(
            info = globalManager.component.item(Material.ITEM_FRAME) {
                it.setDisplayName("&b[&5系统&b] &6称号咨询".colorize())
                it.lore = arrayListOf(
                    "&7已解锁称号数 &f- &a${playerTitles.size}",
                    "&7所有称号数 &f-  &a${titles.size}"
                ).colorize()
            },
            views,
        )
    }

}