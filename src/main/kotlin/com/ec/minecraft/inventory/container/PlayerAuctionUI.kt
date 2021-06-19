package com.ec.minecraft.inventory.container

import com.ec.database.Malls
import com.ec.database.Players
import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.IteratorItem
import com.ec.extension.inventory.component.IteratorUI
import com.ec.extension.inventory.component.IteratorUIProps
import com.ec.util.DoubleUtil.roundTo
import com.ec.util.QueryUtil.iterator
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class PlayerAuctionUI: IteratorUI<Unit>("player-auction") {

    override fun info(props: IteratorUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6个人拍卖".colorize()
        )
    }

    override fun props(player: HumanEntity): IteratorUIProps {
        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        val count = transaction {
            return@transaction Malls.select { Malls.playerId eq ecPlayer.database[Players.id] }.count()
        }

        return IteratorUIProps(
            info = globalManager.component.item(Material.CHEST) {
                it.setDisplayName("&b[&5系统&b] &6拍卖咨询".colorize())
                it.lore = arrayListOf(
                    "&7可拍卖数 &f- &a${ecPlayer.database[Players.auctionLimit]}",
                    "&7已拍卖数 &f- &a${count}"
                ).colorize()
            },
            extras = listOf(
                div(DivProps(
                    style = styleOf {
                        width = 1.px
                        height = 1.px
                    },
                    item = globalManager.component.item(Material.OAK_SIGN) {
                        it.setDisplayName("&b[&5系统&b] &6拍卖物品".colorize())
                        it.lore = arrayListOf(
                            "&f手上拿着您要卖的东西，然后",
                            "&f使用指令 /sell <价格> 来进行拍卖"
                        ).colorize()
                    },
                ))
            ),
            itemsGetter = { cursor -> transaction {
                Malls.select { Malls.playerId eq ecPlayer.database[Players.id]}
                    .iterator(Malls.id, 42, cursor) }
            },
            itemMapper = {
                val display = it[Malls.item]

                display.itemMeta<ItemMeta> {
                    val lores = lore ?: mutableListOf()

                    lores.add("&7--------------------------------")
                    lores.add("&a点击后将下架物品")
                    lores.add("")
                    lores.add("&1库存 - &e${it[Malls.amount]}")
                    lores.add("&1价格 &f(单价/总价) - &e${it[Malls.price]} &f/ &e${(it[Malls.price] * it[Malls.amount]).roundTo(2)} ")
                    lores.add("&7--------------------------------")
                    lore = lores.colorize()
                }

                IteratorItem(
                    item = display,
                    click = { _ ->
                        transaction {
                            val target = Malls.select { Malls.id eq it[Malls.id] }.singleOrNull()
                            when {
                                target == null -> {
                                    player.sendMessage(globalManager.message.system("物品已经出售了，无法进行下架。"))
                                }
                                target[Malls.amount] != it[Malls.amount] -> {
                                    player.sendMessage(globalManager.message.system("物品咨询已刷新，刷新后重试。"))
                                }
                                else -> {
                                    val item = it[Malls.item]
                                    item.amount = it[Malls.amount]
                                    Malls.deleteWhere { Malls.id eq it[Malls.id] }
                                    player.inventory.addItem(item)
                                }
                            }
                            refresh()
                        }
                    }
                )
            }
        )
    }

}