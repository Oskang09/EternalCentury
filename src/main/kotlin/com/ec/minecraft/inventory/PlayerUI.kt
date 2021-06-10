package com.ec.minecraft.inventory

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.UIProvider
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.resquare.dom.childrenOf
import dev.reactant.resquare.dom.declareComponent
import dev.reactant.resquare.dom.unaryPlus
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import dev.reactant.resquare.render.useCancelRawEvent
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class PlayerUI: UIProvider<PlayerUI.PlayerUIProps>("player") {


    data class PlayerUIProps(
        val data: List<PlayerUIPropsData>,
    )

    data class PlayerUIPropsData(
        val material: Material,
        val display: String,
        val routeTo: String
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

    override fun info(props: PlayerUIProps): UIBase {
        return UIBase(
            rows = 3,
            cols = 9,
            title = "&b[&5系统&b] &6玩家主页".colorize()
        )
    }

    override fun props(player: HumanEntity): PlayerUIProps {
        return PlayerUIProps(
            data = listOf(
                PlayerUIPropsData(
                    material = Material.NAME_TAG,
                    display = "&f&l前往 &b[&5系统&b] &6称号列表",
                    routeTo = "title"
                ),
                PlayerUIPropsData(
                    material = Material.ITEM_FRAME,
                    display = "&f&l前往 &b[&5系统&b] &6每日签到",
                    routeTo = "vote"
                )
            )
        )
    }

    override val isStaticProps: Boolean = true
    override val render = declareComponent<PlayerUIProps> { props ->

        useCancelRawEvent()

        div(
            DivProps(
            style = styles.container,
            item = ItemStack(Material.WHITE_STAINED_GLASS_PANE),
            children = childrenOf(

                +(props.data.map {
                    val item = ItemStack(it.material)
                    item.itemMeta<ItemMeta> {
                        setDisplayName(it.display.colorize())
                    }

                    return@map div(DivProps(
                        style = styles.item,
                        item = item,
                        onClick = { event ->
                            globalManager.inventory.displayTo(event.whoClicked, it.routeTo)
                        }
                    ))
                })
            )
        )
        )
    }

}