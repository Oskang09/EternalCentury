package com.ec.minecraft.inventory

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.UIProvider
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.SlotIterator
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TitleUI: UIProvider("title") {

    override fun info(player: Player): UIBase {
        return UIBase(
            title = "§b[§5系统§b] §6称号列表"
        )
    }

    override fun init(player: Player, contents: InventoryContents) {
        val ecPlayer = globalManager.players.getByPlayer(player)!!
        val playerTitles = ecPlayer.getTitles()
        val paginator = contents.pagination()
        val next = paginator.next().page
        val prev = paginator.previous().page
        val allTitles = globalManager.titles.getTitles()
            .sortedBy { it.position }
            .map {
                if (playerTitles.contains(it.id)) {
                    var display = it.uiDisplay(ItemStack(Material.NAME_TAG))
                    if (ecPlayer.getCurrentTitle() == it.id) {
                        display = globalManager.component.withGlow(display) { meta ->
                            meta.lore?.add("")
                            meta.lore?.add(" §6--- §1目前称号使用中 §6---")
                        }
                    }
                    ClickableItem.empty(display)
                } else {
                    ClickableItem.empty(it.uiDisplay(ItemStack(Material.BARRIER)))
                }
            }
            .toTypedArray()

        paginator.setItems(*allTitles)
        paginator.setItemsPerPage(27)
        paginator.addToIterator(contents.newIterator(SlotIterator.Type.VERTICAL, 0, 2))

        contents.set(
            0, 0,
            ClickableItem.empty(globalManager.component.playerHead(player) {
                it.setDisplayName("§b[§5系统§b] §6称号咨询")
                it.lore = arrayListOf(
                    "§7已解锁称号数 §0- §a${ecPlayer.getAvailableTitles().size}",
                    "§7所有称号数 §0-  §a${allTitles.size}"
                )
            })
        )

        contents.set(0, 0, ClickableItem.empty(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
        contents.fillColumn(1, ClickableItem.empty(ItemStack(Material.WHITE_STAINED_GLASS_PANE)))
        if (!paginator.isFirst) {
            contents.set(2, 0, ClickableItem.of(globalManager.component.arrowPrevious(prev)) {
                this.displayTo(player, prev)
            })
        }

        if (!paginator.isLast) {
            contents.set(6, 0, ClickableItem.of(globalManager.component.arrowNext(next)) {
                this.displayTo(player, next)
            })
        }
    }

    override fun update(player: Player, contents: InventoryContents) {
    }
}