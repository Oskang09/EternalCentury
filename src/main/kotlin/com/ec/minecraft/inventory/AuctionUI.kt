package com.ec.minecraft.inventory

import com.ec.database.Malls
import com.ec.database.Players
import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.IteratorItem
import com.ec.extension.inventory.component.IteratorUI
import com.ec.extension.inventory.component.IteratorUIProps
import com.ec.extension.inventory.component.PaginationUIProps
import com.ec.util.DoubleUtil.roundTo
import com.ec.util.QueryUtil.iterator
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class AuctionUI: IteratorUI<AuctionUI.AuctionUIProps>("auction") {

    data class AuctionUIProps(
        val material: Material? = null,
        val nativeId: String? = null,
    )

    override fun info(props: IteratorUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6拍卖商场".colorize()
        )
    }

    override fun props(player: HumanEntity): IteratorUIProps {
        return props(player, null)
    }

    override fun props(player: HumanEntity, props: AuctionUIProps?): IteratorUIProps {
        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        var query = Malls.selectAll()
        var filterType = "全部"
        var filterResult = "NULL"
        if (props != null) {
            if (props.material != null) {
                query = Malls.select { Malls.material eq props.material }
                filterType = "物品材料"
                filterResult = props.material.name
            }

            if (props.nativeId != null) {
                query = Malls.select { Malls.id eq props.nativeId }
                filterType = "内置ID"
                filterResult = props.nativeId
            }
        }

        return IteratorUIProps(
            info = globalManager.component.item(Material.CHEST) {
                it.setDisplayName("&b[&5系统&b] &6商场咨询".colorize())
                it.lore = arrayListOf(
                    "&7分类类别 &f- &a${filterType}",
                    "&7分类数值 &f- &a${filterResult}",
                    "&7拥有金钱 &f- &a${ecPlayer.database[Players.balance].balance}"
                ).colorize()
            },
            itemsGetter = { cursor -> transaction { query.iterator(Malls.id, 42, cursor) } },
            itemMapper = {
                val display = it[Malls.item]

                display.itemMeta<ItemMeta> {
                    val lores = lore ?: mutableListOf()
                    lores.add("&7--------------------------------")
                    lores.add("&a点击进行购买，SHIFT键一次性购买全部。")
                    lores.add("")
                    lores.add("&1卖家 - &e${it[Malls.playerName]}")
                    lores.add("&1价格 &f(单价/总价) - &e${it[Malls.price]} &f/ &e${(it[Malls.price] * it[Malls.amount]).roundTo(2)} ")
                    lores.add("&7--------------------------------")
                    lore = lores.colorize()
                }

                 IteratorItem(
                     item = display,
                     click = {

                     }
                 )
            },
            extras = listOf(

            )
        )
    }

}