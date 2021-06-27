package com.ec.minecraft.title

import com.ec.manager.GlobalManager
import com.ec.manager.title.TitleAPI
import com.ec.model.player.ECPlayer
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ExampleTitle: TitleAPI("test", 0) {

    override fun getItemStack(stack: ItemStack): ItemStack {
        stack.itemMeta<ItemMeta> {
            setDisplayName(("&a称号 - &r" + getDisplay()).colorize())
            lore = listOf(
                "&7&l --- &f&l称号介绍 &7&l--- ",
                "&7&l --- &f&l特殊效果 &7&l--- ",
                "&7&l --- &f&l解锁条件 &7&l--- ",
            ).colorize()
        }
        return stack
    }

    override fun getDisplay(): String {
        return "&f[&e特殊&f]".colorize()
    }

    override fun unlockCondition(ecPlayer: ECPlayer): Boolean {
        return true
    }

    override fun afterUnlock(ecPlayer: ECPlayer) {

    }

}
