package com.ec.minecraft.inventory.container

import com.ec.database.Players
import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.PaginationItem
import com.ec.extension.inventory.component.PaginationUI
import com.ec.extension.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.update


class TitleUI: PaginationUI("title") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6称号列表".colorize()
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        val availableTitles = ecPlayer.getTitles()
        val titles = globalManager.titles.getTitles()
        val views = titles.sortedBy { it.position }
            .map { title ->
                if (availableTitles.contains(title.id)) {
                    var display = title.getItemStack(ItemStack(Material.NAME_TAG))
                    if (ecPlayer.database[Players.currentTitle] == title.id) {
                        display = globalManager.component.withGlow(display) { meta ->
                            val lores = meta.lore ?: mutableListOf()
                            lores.add("")
                            lores.add(" &6--- &1目前称号使用中 &6---".colorize())
                            meta.lore = lores
                        }
                    }

                    return@map PaginationItem(display) { _ ->
                        val titleDisplay = title.getDisplay(player)
                        player.setDisplayName(titleDisplay)
                        player.setPlayerListName(titleDisplay)

                        ecPlayer.ensureUpdate("update title to ${title.id}") {
                            Players.update({ Players.id eq ecPlayer.database[Players.id]}) {
                                it[currentTitle] = title.id
                            }
                        }

                        refresh()
                    }
                } else {
                    return@map PaginationItem(
                        item =  title.getItemStack(ItemStack(Material.BARRIER))
                    )
                }
            }

        return PaginationUIProps(
            info = globalManager.component.item(Material.ITEM_FRAME) {
                it.setDisplayName("&b[&5系统&b] &6称号咨询".colorize())
                it.lore = arrayListOf(
                    "&7已解锁称号数 &f- &a${availableTitles.size}",
                    "&7所有称号数 &f-  &a${titles.size}"
                ).colorize()
            },
            views,
        )
    }

}