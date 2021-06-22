package com.ec.minecraft.title

import com.ec.manager.GlobalManager
import com.ec.manager.title.TitleAPI
import com.ec.model.player.ECPlayer
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ExampleTitle: TitleAPI("some_id", 0) {
    override fun initialize(globalManager: GlobalManager) {

    }

    override fun getItemStack(stack: ItemStack): ItemStack {
        stack.itemMeta<ItemMeta> {
            setDisplayName(("&a称号 - &r").colorize())
            lore = listOf(
                "&7&l --- &f&l称号介绍 &7&l--- ",
                "&7&l --- &f&l特殊效果 &7&l--- ",
                "&7&l --- &f&l解锁条件 &7&l--- ",
            ).colorize()
        }
        return stack
    }

    override fun getDisplay(player: Player): String {
        return ""
    }

    override fun unlockCondition(ecPlayer: ECPlayer): Boolean {
        return true
    }

    override fun afterUnlock(ecPlayer: ECPlayer) {

    }

}
