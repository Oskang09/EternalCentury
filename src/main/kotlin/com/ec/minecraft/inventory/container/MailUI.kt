package com.ec.minecraft.inventory.container

import com.ec.database.Mails
import com.ec.database.Players
import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.IteratorItem
import com.ec.manager.inventory.component.IteratorUI
import com.ec.manager.inventory.component.IteratorUIProps
import com.ec.util.QueryUtil.iterator
import com.ec.util.StringUtil.toComponent
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class MailUI: IteratorUI<Unit>("mail") {

    override fun info(props: IteratorUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6邮件快递"
        )
    }

    override fun props(player: HumanEntity): IteratorUIProps {
        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        return IteratorUIProps(
            info = globalManager.component.playerHead(player) {
                it.displayName("&b[&5系统&b] &6邮箱快递".toComponent())
            },
            extras = {
                listOf(
                    div(DivProps(
                        style = styleOf {
                            width = 1.px
                            height = 1.px
                        },
                        item = globalManager.component.item(Material.BARRIER) {
                            it.displayName("&b[&5系统&b] &6清理邮箱".toComponent())
                            it.lore(arrayListOf("&f此操作只会清理已读的邮件.").toComponent())
                        },
                        onClick = { _ ->
                            transaction {
                                Mails.select { Mails.playerId eq ecPlayer.database[Players.id] }
                                    .andWhere { Mails.isRead eq true }
                                    .forEach { Mails.deleteWhere { Mails.id eq it[Mails.id] } }
                            }
                            it.refreshState()
                        }
                    ))
                )
            },
            itemsGetter = { cursor -> transaction {
                Mails.select { Mails.playerId eq ecPlayer.database[Players.id] }
                    .orderBy(Mails.createdAt to SortOrder.DESC)
                    .iterator(Mails.id, 42, cursor) }
            },
            itemMapper = { it, controller ->
                val isRead = it[Mails.isRead]
                var material = Material.MINECART
                if (!isRead) {
                    material = Material.CHEST_MINECART
                }

                IteratorItem(
                    click = { evt ->
                        if (!it[Mails.isRead]) {
                            val clickedPlayer = evt.whoClicked as Player
                            if (it[Mails.rewards].size > 0) {
                                globalManager.sendRewardToPlayer(clickedPlayer, it[Mails.rewards])
                                player.inventory.addItem(*it[Mails.item].toTypedArray())
                            }

                            transaction {
                                Mails.update({ Mails.id eq it[Mails.id]}) { mail ->
                                    mail[Mails.isRead] = true
                                }
                            }
                            controller.refreshState()
                        }
                    },
                    item = globalManager.component.item(material) { meta ->
                        meta.displayName(it[Mails.title].toComponent())
                        val lore = it[Mails.content]
                        if (it[Mails.rewards].size > 0) {
                            lore.add("")
                            lore.add("&f快递物件 &b>")
                            it[Mails.item].forEach { item ->
                                val name = item.itemMeta?.displayName ?: item.itemMeta?.localizedName ?: item.type.toString()
                                val amount = item.amount
                                lore.add("&f${amount}x $name")
                            }
                            it[Mails.rewards].forEach { reward ->
                                lore.add(reward.display)
                            }
                        }
                        meta.lore(lore.toComponent())
                    }
                )
            }
        )
    }

}