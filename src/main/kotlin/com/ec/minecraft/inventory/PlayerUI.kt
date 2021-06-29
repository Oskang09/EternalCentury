package com.ec.minecraft.inventory

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.UIProvider
import com.ec.util.StringUtil.toComponent
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
import org.bukkit.entity.Player
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
            rows = 4,
            cols = 9,
            title = "&b[&5系统&b] &6玩家主页"
        )
    }

    override fun props(player: HumanEntity): PlayerUIProps {
        val display = mutableListOf(
            PlayerUIPropsData(
                material = Material.NAME_TAG,
                display = "&f&l前往 &b[&5系统&b] &6称号列表",
                routeTo = "title"
            ),
            PlayerUIPropsData(
                material = Material.BOOK,
                display = "&f&l前往 &b[&5系统&b] &6每日签到",
                routeTo = "vote"
            ),
            PlayerUIPropsData(
                material = Material.CHEST_MINECART,
                display = "&f&l前往 &b[&5系统&b] &6钱包咨询",
                routeTo = "wallet"
            ),
            PlayerUIPropsData(
                material = Material.END_PORTAL_FRAME,
                display = "&f&l前往 &b[&5系统&b] &6伺服传送",
                routeTo = "teleport"
            ),
            PlayerUIPropsData(
                material = Material.MINECART,
                display = "&f&l前往 &b[&5系统&b] &6邮件快递",
                routeTo = "mail"
            ),
            PlayerUIPropsData(
                material = Material.GOLDEN_APPLE,
                display = "&f&l前往 &b[&5系统&b] &6伺服赞助",
                routeTo = "payment"
            ),
            PlayerUIPropsData(
                material = Material.PLAYER_HEAD,
                display = "&f&l前往 &b[&5系统&b] &6玩家造型",
                routeTo = "skin"
            ),
            PlayerUIPropsData(
                material = Material.BLAZE_POWDER,
                display = "&f&l前往 &b[&5系统&b] &6粒子特效",
                routeTo = "command:pp gui"
            ),
            PlayerUIPropsData(
                material = Material.NETHER_STAR,
                display = "&f&l前往 &b[&5系统&b] &6抽奖水池",
                routeTo = "crate"
            ),
            PlayerUIPropsData(
                material = Material.CHEST,
                display = "&f&l前往 &b[&5系统&b] &6拍卖咨询",
                routeTo = "player-auction"
            ),
            PlayerUIPropsData(
                material = Material.OAK_SIGN,
                display = "&f&l前往 &b[&5系统&b] &6活动咨询",
                routeTo = "activity"
            ),
            PlayerUIPropsData(
                material = Material.ENDER_CHEST,
                display = "&f&l前往 &b[&5系统&b] &6随身末影盒",
                routeTo = "command:ec"
            ),
            PlayerUIPropsData(
                material = Material.CRAFTING_TABLE,
                display = "&f&l前往 &b[&5系统&b] &6随身工作台",
                routeTo = "command:wb"
            )
        )
        return PlayerUIProps(display)
    }

    override val isStaticProps = true
    override val render = declareComponent<PlayerUIProps> { props ->

        useCancelRawEvent()

        div(DivProps(
            style = styles.container,
            item = ItemStack(Material.WHITE_STAINED_GLASS_PANE),
            children = childrenOf(
                +(props.data.map {
                    val item = ItemStack(it.material)
                    item.itemMeta<ItemMeta> {
                        displayName(it.display.toComponent())
                    }

                    return@map div(DivProps(
                        style = styles.item,
                        item = item,
                        onClick = { event ->
                            val player = event.whoClicked as Player
                            if (it.routeTo.startsWith("command:")) {
                                player.performCommand(it.routeTo.replace("command:", ""))
                            } else {
                                globalManager.inventory.displayTo(player, it.routeTo)
                            }
                        }
                    ))
                })
            )
        ))
    }

}