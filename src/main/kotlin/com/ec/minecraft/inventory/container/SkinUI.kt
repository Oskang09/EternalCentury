package com.ec.minecraft.inventory.container

import com.ec.ECCore
import com.ec.database.Players
import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.component.PaginationItem
import com.ec.extension.inventory.component.PaginationUI
import com.ec.extension.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.meta.SkullMeta

class SkinUI: PaginationUI("skin") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6玩家造型".colorize(),
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        val skinLimit = ecPlayer.database[Players.skinLimit]
        val availableSkins = ecPlayer.database[Players.skins]
        val currentSkin = globalManager.skins.getSkinName(player.name)

        availableSkins.add(player.name)
        val views = availableSkins.map { skinName ->
            var item = globalManager.component.playerHead(skinName) {
                it.setDisplayName("&f[&5造型&f] &f$skinName".colorize())
            }
            if (currentSkin == skinName) {
                item = globalManager.component.withGlow(item)
            }

            return@map PaginationItem(item) {
                globalManager.skins.setSkinName(player.name, skinName)
            }
        }

        return PaginationUIProps(
            info = globalManager.component.playerHead(player) {
                it.setDisplayName("&b[&5系统&b] &6玩家造型".colorize())
                it.lore = arrayListOf(
                    "&7已拥有造型数 &f- &a${availableSkins.size - 1}",
                    "&7可拥有造型数 &f-  &a${skinLimit}"
                ).colorize()
            },
            views,
            extras = if (skinLimit == availableSkins.size - 1) null else div(DivProps(
                style = styleOf {
                    width = 1.px
                    height = 1.px
                },
                item = globalManager.component.item(Material.PLAYER_HEAD) {
                    it.setDisplayName("&b[&5系统&b] &6添加新造型".colorize())
                    it.lore = arrayListOf(
                        "&f1. 请确认名字正确后才添加",
                        "&f2. 添加错误将无法更改",
                    ).colorize()
                },
                onClick = {
                    AnvilGUI.Builder()
                        .onComplete{ player, text ->
                            AnvilGUI.Response.close()
                        }
                        .onClose { this.displayTo(it) }
                        .itemLeft(globalManager.component.item(Material.PLAYER_HEAD))
                        .text("")
                        .title("&f[&5系统&f] &0输入您要的造型名称".colorize())
                        .plugin(ECCore.instance)
                        .open(player)
                }
            ))
        )
    }
}