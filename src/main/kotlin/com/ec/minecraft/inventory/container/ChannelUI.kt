package com.ec.minecraft.inventory.container

import com.ec.database.Players
import com.ec.database.model.ChatType
import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.UIProvider
import com.ec.model.player.ECPlayer
import com.ec.util.ModelUtil.toDisplay
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.resquare.dom.childrenOf
import dev.reactant.resquare.dom.declareComponent
import dev.reactant.resquare.dom.unaryPlus
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import dev.reactant.resquare.render.useCancelRawEvent
import dev.reactant.resquare.render.useEffect
import dev.reactant.resquare.render.useState
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.exposed.sql.update

class ChannelUI: UIProvider<ChannelUI.ChannelUIProps>("channel") {

    data class ChannelUIProps(val player: ECPlayer)

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


    override fun info(props: ChannelUIProps): UIBase {
        return UIBase(
            rows = 4,
            cols = 9,
            title = "&b[&5系统&b] &6聊天频道".colorize()
        )
    }

    override fun props(player: HumanEntity): ChannelUIProps {
        return ChannelUIProps(globalManager.players.getByPlayer(player as Player))
    }

    private fun renderActive(it: ChatType): ItemStack {
        val item = ItemStack(Material.GREEN_WOOL)
        item.itemMeta<ItemMeta> {
            setDisplayName(("&b[&a开启中&b] &6${it.toDisplay()}").colorize())
        }
        return item
    }

    private fun renderInactive(it: ChatType): ItemStack {
        val item = ItemStack(Material.RED_WOOL)
        item.itemMeta<ItemMeta> {
            setDisplayName(("&b[&c关闭中&b] &6${it.toDisplay()}").colorize())
        }
        return item
    }

    override val render = declareComponent<ChannelUIProps> { props ->
        useCancelRawEvent()

        val (channels, setChannels) = useState(props.player.database[Players.channels])

        useEffect({
            return@useEffect {
                props.player.ensureUpdate("ChannelUI.update player channel", true) {
                    Players.update({ Players.id eq props.player.database[Players.id] }) {
                        it[Players.channels] = channels
                    }
                }
            }
        }, arrayOf())

        div(DivProps(
            style = styles.container,
            item = ItemStack(Material.WHITE_STAINED_GLASS_PANE),
            children = childrenOf(
                +(ChatType.values().map {
                    div(DivProps(
                        style = styles.item,
                        item = if (channels.contains(it)) renderActive(it)
                        else renderInactive(it),
                        onClick = { _ ->
                            if (channels.contains(it)) {
                                channels.remove(it)
                            } else {
                                channels.add(it)
                            }
                            setChannels(channels)
                        }
                    ))
                })
            )
        ))
    }
}