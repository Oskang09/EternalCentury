package com.ec.minecraft.admin

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.UIProvider
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

class AdminUI : UIProvider<AdminUI.AdminUIProps>("admin") {

    data class AdminUIPropsData(
        val material: Material,
        val display: String,
        val routeTo: String
    )

    data class AdminUIProps(
        val data: List<AdminUIPropsData>,
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

    override fun info(props: AdminUIProps): UIBase {
        return UIBase(
            rows = 3,
            cols = 9,
            title = "&b[&5系统&b] &6管理控制台".colorize()
        )
    }

    override fun props(player: HumanEntity): AdminUIProps {
        return AdminUIProps(
            data = listOf(
                AdminUIPropsData(
                    material = Material.ENCHANTED_BOOK,
                    display = "&f&l前往 &b[&5系统&b] &6技能附魔书",
                    routeTo = "admin-enchantment"
                ),
                AdminUIPropsData(
                    material = Material.ITEM_FRAME,
                    display = "&f&l前往 &b[&5系统&b] &6物品列表",
                    routeTo = "admin-item"
                )
            )
        )
    }

    override val isStaticProps: Boolean = true
    override val render = declareComponent<AdminUIProps> { props ->

        useCancelRawEvent()

        div(DivProps(
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
        ))
    }
}