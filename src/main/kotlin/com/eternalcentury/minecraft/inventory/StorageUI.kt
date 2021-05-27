package com.eternalcentury.minecraft.inventory

import com.eternalcentury.inventory.UIBase
import com.eternalcentury.inventory.UIComponent
import com.eternalcentury.inventory.UIProvider
import dev.reactant.reactant.core.component.Component
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.content.InventoryContents
import org.bukkit.entity.Player

@Component
class StorageUI(private val component: UIComponent): UIProvider() {

    override fun info(player: Player): UIBase {
        return UIBase(

        )
    }

    override fun init(player: Player, contents: InventoryContents) {
        contents.set(
            0, 0,
            ClickableItem.empty(component.playerHead(player) {
                it.setDisplayName("")
            })
        )
    }

    override fun update(player: Player, contents: InventoryContents) {
    }
}