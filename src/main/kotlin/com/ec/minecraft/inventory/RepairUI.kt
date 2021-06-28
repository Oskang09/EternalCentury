package com.ec.minecraft.inventory

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.UIProvider
import com.ec.util.DoubleUtil.roundTo
import dev.reactant.resquare.dom.childrenOf
import dev.reactant.resquare.dom.declareComponent
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import dev.reactant.resquare.render.useCancelRawEvent
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

class RepairUI: UIProvider<RepairUI.RepairUIProps>("repair") {

    data class RepairUIProps(
        val item: ItemStack,
        val price: Double,
    )

    private val styles = object {

        val marginItem = styleOf {
            marginLeft = 1.px
            width = 1.px
            height = 1.px
        }

    }

    override fun info(props: RepairUIProps): UIBase {
        return UIBase(
            title = ("&b[&5系统&b] &f修理花费 &e- &6&l" + (props.price.roundTo(2))),
            rows = 1,
            cols = 9
        )
    }

    override fun props(player: HumanEntity): RepairUIProps {
        var repairRequired = 0.0
        val mainHand = player.inventory.itemInMainHand
        if (mainHand.hasItemMeta() && mainHand.itemMeta is Damageable) {
            val meta = mainHand.itemMeta as Damageable

            val currentDamage = meta.damage
            val maxDurability = mainHand.type.maxDurability
            val repairDamage = maxDurability - currentDamage

            val nbt = globalManager.items.deserializeFromItem(mainHand)
            nbt?.enchantments?.forEach { (_, level) ->
                repairRequired += (level * globalManager.serverConfig.repairPrice)
            }

            repairRequired = ((globalManager.serverConfig.repairPrice * repairDamage) * globalManager.serverConfig.repairRate).toDouble()
        }
        return RepairUIProps(mainHand, repairRequired )
    }

    override val render = declareComponent<RepairUIProps> { props ->
        useCancelRawEvent()

        div(DivProps(
            style = styleOf {
                width = 9.px
                height = 1.px
                marginLeft = 1.px
                marginRight = 1.px
            },
            children = childrenOf(
                div(DivProps(
                    style= styles.marginItem,
                    item = globalManager.component.woolAccept(),
                    onClick = { event ->
                        val entity = event.whoClicked

                        entity.world.playSound(entity.location,  Sound.BLOCK_ANVIL_USE, 1F, 0F)
                        val meta = props.item.itemMeta as Damageable
                        meta.damage = 0
                        props.item.itemMeta = (meta as ItemMeta)
                        entity.closeInventory()
                    },
                )),
                div(DivProps(
                    style = styles.marginItem,
                    item = props.item,
                )),
                div(DivProps(
                    style= styles.marginItem,
                    item = globalManager.component.woolDecline(),
                    onClick = { event ->
                        event.whoClicked.closeInventory()
                    },
                ))
            )
        ))
    }
}