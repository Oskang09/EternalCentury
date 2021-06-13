package com.ec.minecraft.inventory

import com.ec.database.Mails
import com.ec.database.Players
import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.IteratorItem
import com.ec.extension.inventory.component.IteratorUI
import com.ec.extension.inventory.component.IteratorUIProps
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class MailUI: IteratorUI("mail") {

    override fun info(props: IteratorUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6邮件快递".colorize()
        )
    }

    override fun props(player: HumanEntity): IteratorUIProps {
        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        val query = Mails.select { Mails.playerId eq ecPlayer.database[Players.id] }
        val numOfMails = transaction { query.count() }

        return IteratorUIProps(
            info = globalManager.component.playerHead(player) {
                it.setDisplayName("&b[&5系统&b] &6邮箱快递".colorize())
                it.lore = arrayListOf("&7总邮箱数 &f- &a${numOfMails}").colorize()
            },
            itemsCount = numOfMails,
            itemsPerPage = 42,
            itemsGetter = { page -> transaction {
                query.limit(42, (42 * page).toLong())
                    .orderBy(Mails.createdAt to SortOrder.DESC)
                    .toList()
                    .map {
                        val hasRewards = it[Mails.rewards] != null
                        val isRead = it[Mails.isRead]
                        var material = Material.MINECART
                        if (hasRewards && !isRead) {
                            material = Material.CHEST_MINECART
                        }

                        return@map IteratorItem(
                            click = { evt ->
                                if (!it[Mails.isRead]) {
                                    val clickedPlayer = evt.whoClicked as Player
                                    if (hasRewards) {
                                        globalManager.sendRewardToPlayer(it[Mails.rewards]!!, clickedPlayer)
                                    }

                                    transaction {
                                        Mails.update({ Mails.id eq it[Mails.id]}) { mail ->
                                            mail[Mails.isRead] = true
                                        }
                                    }
                                    refresh()
                                }
                            },
                            item = globalManager.component.item(material) { meta ->
                                meta.setDisplayName(it[Mails.title].colorize())
                                meta.lore = it[Mails.content].split("\n")
                                if (hasRewards) {
                                    meta.lore?.add("&f&l快递物件 &b&l>")
                                    it[Mails.rewards]?.forEach { reward ->
                                        meta.lore?.add(reward.display)
                                    }
                                }
                                meta.lore = meta.lore!!.colorize()
                            }
                        )
                    }
                }
            }
        )
    }

}