package com.ec.minecraft.inventory.container

import com.ec.database.Players
import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Team
import org.jetbrains.exposed.sql.update

class TitleUI: PaginationUI<Unit>("title") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6称号列表".colorize()
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        globalManager.runOffMainThread {
            globalManager.titles.checkPlayerTitleAvailability(player as Player)
        }

        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        val availableTitles = ecPlayer.getTitles()
        val titles = globalManager.titles.getTitles()
        return PaginationUIProps(
            info = globalManager.component.item(Material.ITEM_FRAME) {
                it.setDisplayName("&b[&5系统&b] &6称号咨询".colorize())
                it.lore = arrayListOf(
                    "&7已解锁称号数 &f- &a${availableTitles.size}",
                    "&7所有称号数 &f-  &a${titles.size}"
                ).colorize()
            },
            {
                titles.sortedBy { it.position }
                    .map { title ->
                        if (availableTitles.contains(title.id)) {
                            var display = title.getItemStack(ItemStack(Material.NAME_TAG))
                            if (ecPlayer.database[Players.currentTitle] == title.id) {
                                display = globalManager.component.withGlow(display) { meta ->
                                    val lores = meta.lore ?: mutableListOf()
                                    lores.add("")
                                    lores.add(" &7--- &e目前称号使用中 &7---".colorize())
                                    meta.lore = lores
                                }
                            }

                            return@map PaginationItem(display) { _ ->
                                val titleDisplay = title.getDisplay()
                                player.setDisplayName(titleDisplay + " " + player.name)
                                player.setPlayerListName(titleDisplay + " " + player.name)

                                val nameKey = player.name
                                val board = player.scoreboard
                                val team = board.getTeam(nameKey) ?: board.registerNewTeam(nameKey)
                                team.prefix = "$titleDisplay "
                                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS)
                                team.addEntry(player.name)
                                player.scoreboard = board

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
            },
        )
    }

}