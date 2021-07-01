package com.ec.minecraft.inventory

import com.ec.database.MallHistories
import com.ec.database.Malls
import com.ec.database.Players
import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.IteratorItem
import com.ec.manager.inventory.component.IteratorUI
import com.ec.manager.inventory.component.IteratorUIProps
import com.ec.manager.wallet.WalletManager
import com.ec.util.DoubleUtil.roundTo
import com.ec.util.QueryUtil.iterator
import com.ec.util.StringUtil.generateUniqueID
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class AuctionUI: IteratorUI<AuctionUI.AuctionUIProps>("auction") {

    data class AuctionUIProps(
        val material: Material? = null,
        val nativeId: String? = null,
    )

    override fun info(props: IteratorUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6拍卖商场"
        )
    }

    override fun props(player: HumanEntity): IteratorUIProps {
        return props(player, null)
    }

    override fun props(player: HumanEntity, props: AuctionUIProps?): IteratorUIProps {
        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        val query = Malls.select { Malls.playerId neq  ecPlayer.database[Players.id ] }
        var filterType = "全部"
        var filterResult = "NULL"
        if (props != null) {
            if (props.material != null) {
                query.andWhere { Malls.material eq props.material }
                filterType = "物品材料"
                filterResult = props.material.name
            }

            if (props.nativeId != null) {
                query.andWhere { Malls.id eq props.nativeId }
                filterType = "内置ID"
                filterResult = props.nativeId
            }
        }

        return IteratorUIProps(
            info = globalManager.component.item(Material.CHEST) {
                it.displayName("&b[&5系统&b] &6商场咨询".toComponent())
                it.lore(arrayListOf(
                    "&7分类类别 &f- &a${filterType}",
                    "&7分类数值 &f- &a${filterResult}",
                    "&7拥有金钱 &f- &a${globalManager.wallets.playerWallet(player.name, WalletManager.ECONOMY_WALLET).balance}"
                ).toComponent())
            },
            extras = listOf(
                div(DivProps(
                    style = styleOf {
                        width = 1.px
                        height = 1.px
                    },
                    item = globalManager.component.item(Material.NETHER_STAR) {
                        it.displayName("&f[&5系统&f] &a分类物品".toComponent())
                        it.lore(arrayListOf("&f点击后会显示界面选择物品进行分类即可").toComponent())
                    },
                    onClick = { _ ->
                        globalManager.inventory.displayItemFilter(player) { material, id ->
                            displayWithProps(player, AuctionUIProps(
                                material = material,
                                nativeId = id,
                            ))
                        }
                    }
                ))
            ),
            itemsGetter = { cursor -> transaction { query.iterator(Malls.id, 42, cursor) } },
            itemMapper = {
                val display = it[Malls.item]

                display.amount = 1
                display.itemMeta<ItemMeta> {
                    val lores = lore() ?: mutableListOf()
                    lores.add("&7--------------------------------".toComponent())
                    lores.add("&a点击进行购买，SHIFT键一次性购买全部。".toComponent())
                    lores.add("".toComponent())
                    lores.add("&1库存 - &e${it[Malls.amount]}".toComponent())
                    lores.add("&1卖家 - &e${it[Malls.playerName]}".toComponent())
                    lores.add("&1价格 &f(单价/总价) - &e${it[Malls.price]} &f/ &e${(it[Malls.price] * it[Malls.amount]).roundTo(2)} ".toComponent())
                    lores.add("&7--------------------------------".toComponent())
                    lore(lores)
                }

                 IteratorItem(
                     item = display,
                     click = { evt ->
                         var buyCount = 1
                         if (evt.isShiftClick) {
                            buyCount = it[Malls.amount]
                         }

                         val purchaseAmount = (it[Malls.price] * buyCount).roundTo(2)
                         when {
                             globalManager.economy.has(player, purchaseAmount) -> {
                                 val item = it[Malls.item]
                                 item.amount = buyCount

                                 val target = Malls.select { Malls.id eq it[Malls.id] }.singleOrNull()
                                 when {
                                     target == null -> {
                                         globalManager.message.system("商品已经被购买或者已经下架。")
                                     }
                                     target[Malls.amount] != buyCount -> {
                                         globalManager.message.system("商品咨询已经刷新，刷新后重试。")
                                     }
                                     else -> {
                                         globalManager.economy.withdrawPlayer(player, purchaseAmount)
                                         globalManager.economy.depositPlayer(it[Malls.playerName], purchaseAmount)
                                         transaction {

                                             if (item.amount == it[Malls.amount]) {
                                                 Malls.deleteWhere { Malls.id eq it[Malls.id] }
                                             } else {
                                                 Malls.update({ Malls.id eq it[Malls.id]  }) { mall ->
                                                     mall[amount] = it[amount] - buyCount
                                                 }
                                             }

                                             MallHistories.insert { history ->
                                                 history[id] = "".generateUniqueID()
                                                 history[buyerId] = ecPlayer.database[Players.id]
                                                 history[buyerName] = player.name
                                                 history[sellerId] = it[Malls.playerId]
                                                 history[sellerName] = it[Malls.playerName]
                                                 history[material] = it[Malls.material]
                                                 history[nativeId] = it[Malls.nativeId]
                                                 history[MallHistories.item] = it[Malls.item]
                                                 history[amount] = buyCount
                                                 history[price] = purchaseAmount
                                                 history[historyAt] = Instant.now().epochSecond
                                             }
                                         }

                                         globalManager.givePlayerItem(player.name, listOf(item))
                                     }
                                 }
                                 refresh()
                             }
                             else -> {
                                 player.sendMessage(globalManager.message.system("您没有足够的金钱购买"))
                             }
                         }
                     }
                 )
            }
        )
    }

}