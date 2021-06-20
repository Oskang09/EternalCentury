package com.ec.minecraft.inventory.container

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.PaginationItem
import com.ec.extension.inventory.component.PaginationUI
import com.ec.extension.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
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
            title = "&b[&5系统&b] &6伺服传送".colorize()
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        return PaginationUIProps(
            info = globalManager.component.item(Material.END_PORTAL_FRAME) {
                it.setDisplayName("&b[&5系统&b] &6传送咨询".colorize())
                it.lore = arrayListOf("&7传送点数量 &f-  &a3").colorize()
            },
            listOf(
                PaginationItem(
                    item = globalManager.component.item(Material.OAK_WOOD) {
                       it.setDisplayName("&5&l传送到 &a&l中世纪村庄".colorize())
                        it.lore = arrayListOf(
                            "&5- &f地狱入口",
                            "&5- &f末地入口",
                            "&5- &f铁匠师傅",
                            "&5- &f打工人商店",
                            "&5- &f防疫大使"
                        ).colorize()
                    },
                    click = {
                        player.closeInventory()
                        teleportTo(it.whoClicked as Player, "old-spawn")
                    }
                ),
                PaginationItem(
                    item = globalManager.component.item(Material.QUARTZ_BLOCK) {
                        it.setDisplayName("&5&l传送到 &a&l新世纪城镇".colorize())
                        it.lore = arrayListOf(
                            "&5- &f玩家商店",
                            "&5- &f狩猎贩卖",
                            "&5- &f产物鉴定",
                            "&5- &f防疫大使"
                        ).colorize()
                    },
                    click = {
                        player.closeInventory()
                        teleportTo(it.whoClicked as Player, "modern-spawn")
                    }
                ),
                PaginationItem(
                    item = globalManager.component.item(Material.GRASS_BLOCK) {
                        it.setDisplayName("&5&l传送到 &a&l生存世界".colorize())
                        it.lore = arrayListOf(
                            "&5- &f玩家生存",
                            "&5- &f领地建筑",
                        ).colorize()
                    },
                    click = {
                        player.closeInventory()
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "res rt ${player.name} world")
                    }
                ),
                PaginationItem(
                    item = globalManager.component.item(Material.DIAMOND_PICKAXE) {
                        it.setDisplayName("&5&l传送到 &a&l资源世界".colorize())
                        it.lore = arrayListOf(
                            "&5- &f资源采集",
                            "&5- &f随机传送",
                            "&5- &f定期洗白"
                        ).colorize()
                    },
                    click = {
                        player.closeInventory()
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "res rt ${player.name} survival")
                    }
                ),
            ),
        )
    }
}