package com.ec.minecraft.inventory.container

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.toComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class TeleportUI: PaginationUI<Unit>("teleport") {

    private fun teleportTo(player: Player, target: String) {
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "tpgo ${player.name} $target",
        )
    }

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6伺服传送"
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        val playerState = globalManager.states.getPlayerState(player as Player)
        return PaginationUIProps(
            info = globalManager.component.item(Material.END_PORTAL_FRAME) {
                it.displayName("&b[&5系统&b] &6传送咨询".toComponent())
                it.lore(arrayListOf("&7传送点数量 &f-  &a${4 + playerState.homes.size}").toComponent())
            },
            {
                listOf(
                    PaginationItem(
                        item = globalManager.component.item(Material.OAK_WOOD) {
                            it.displayName("&5&l传送到 &a&l中世纪村庄".toComponent())
                            it.lore(arrayListOf(
                                "&5- &f地狱入口",
                                "&5- &f末地入口",
                                "&5- &f铁匠师傅",
                                "&5- &f打工人商店",
                                "&5- &f防疫大使"
                            ).toComponent())
                        },
                        click = {
                            player.closeInventory()
                            teleportTo(it.whoClicked as Player, "old-spawn")
                        }
                    ),
                    PaginationItem(
                        item = globalManager.component.item(Material.QUARTZ_BLOCK) {
                            it.displayName("&5&l传送到 &a&l新世纪城镇".toComponent())
                            it.lore(arrayListOf(
                                "&5- &f玩家商店",
                                "&5- &f狩猎贩卖",
                                "&5- &f产物鉴定",
                                "&5- &f活动大使",
                                "&5- &f防疫大使"
                            ).toComponent())
                        },
                        click = {
                            player.closeInventory()
                            teleportTo(it.whoClicked as Player, "modern-spawn")
                        }
                    ),
                    PaginationItem(
                        item = globalManager.component.item(Material.GRASS_BLOCK) {
                            it.displayName("&5&l传送到 &a&l地皮世界".toComponent())
                            it.lore(arrayListOf(
                                "&5- &f玩家生存",
                                "&5- &f地皮建筑",
                            ).toComponent())
                        },
                        click = {
                            player.closeInventory()
                            teleportTo(it.whoClicked as Player, "plot-spawn")
                        }
                    ),
                    PaginationItem(
                        item = globalManager.component.item(Material.DIAMOND_PICKAXE) {
                            it.displayName("&5&l传送到 &a&l资源世界".toComponent())
                            it.lore(arrayListOf(
                                "&5- &f资源采集",
                                "&5- &f随机传送",
                                "&5- &f定期洗白"
                            ).toComponent())
                        },
                        click = {
                            player.closeInventory()
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rtp ${player.name} world")
                        }
                    ),
                    *playerState.homes.map { (name, location) ->
                        return@map PaginationItem(
                            item = globalManager.component.item(Material.CHEST) {
                                it.displayName("&5&l传送到 &e家园&f($name)".toComponent())
                                it.lore(arrayListOf(
                                    "&7世界  - &f资源世界",
                                    "&7坐标X - &f${location.location.x}",
                                    "&7坐标Y - &f${location.location.y}",
                                    "&7坐标Z - &f${location.location.z}",
                                ).toComponent())
                            },
                            click = {
                                player.closeInventory()
                                player.teleportAsync(location.location)
                            }
                        )
                    }.toTypedArray()
                )
            }
        )
    }
}