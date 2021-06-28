package com.ec.minecraft.inventory.container

import com.ec.database.Players
import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.InputUtil
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import net.skinsrestorer.api.PlayerWrapper
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.exposed.sql.update

class SkinUI: PaginationUI<Unit>("skin") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6玩家造型",
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        val ecPlayer = globalManager.players.getByPlayer(player as Player)
        val skinLimit = ecPlayer.database[Players.skinLimit]
        val availableSkins = ecPlayer.database[Players.skins]

        availableSkins.add(player.name)
        val views = availableSkins.map { skinName ->
            val profile = globalManager.getPlayerOriginTexture(skinName)
            val item = if (profile == null) {
                globalManager.component.item(Material.PLAYER_HEAD)
            } else {
                globalManager.component.playerHead(profile)
            }

            item.itemMeta<ItemMeta> {
                displayName("&f[&5造型&f] &f$skinName".toComponent())
            }

            return@map PaginationItem(item) {
                globalManager.runOffMainThread {
                    if (profile == null) {
                        globalManager.skins.removeSkin(player.name)
                    } else {
                        globalManager.skins.setSkin(player.name, skinName)
                        globalManager.skins.applySkin(PlayerWrapper(it.whoClicked as Player))
                    }
                }
            }
        }

        return PaginationUIProps(
            info = globalManager.component.playerHead(player) {
                it.displayName("&b[&5系统&b] &6玩家造型".toComponent())
                it.lore(arrayListOf(
                    "&7已拥有造型数 &f- &a${availableSkins.size - 1}",
                    "&7可拥有造型数 &f-  &a${skinLimit}"
                ).toComponent())
            },
            { views },
            extras = listOf(
                if (skinLimit == availableSkins.size - 1) null else div(DivProps(
                    style = styleOf {
                        width = 1.px
                        height = 1.px
                    },
                    item = globalManager.component.item(Material.PLAYER_HEAD) {
                        it.displayName("&b[&5系统&b] &6添加新造型".toComponent())
                        it.lore(arrayListOf(
                            "&f1. 请确认名字正确后才添加",
                            "&f2. 添加错误将无法更改",
                        ).toComponent())
                    },
                    onClick = { _ ->
                        InputUtil.requestString(
                            player, "输入您要的造型名称",
                            { this.displayTo(it) }
                        ) { _, input ->
                            globalManager.getPlayerOriginTexture(input) ?:
                            return@requestString AnvilGUI.Response.text("此玩家没有造型")

                            ecPlayer.ensureUpdate("SkinUI.AnvilGUI.addSkin") {
                                val skinList = ecPlayer.database[Players.skins]
                                skinList.add(input)

                                Players.update({ Players.id eq ecPlayer.database[Players.id] }) {
                                    it[skins] = skinList
                                }
                            }

                            AnvilGUI.Response.close()
                        }
                    }
                ))
            )
        )
    }
}