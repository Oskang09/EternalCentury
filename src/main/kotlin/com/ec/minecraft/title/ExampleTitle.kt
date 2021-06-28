package com.ec.minecraft.title

import com.ec.manager.title.TitleAPI
import com.ec.model.player.ECPlayer
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ExampleTitle: TitleAPI("test", 0) {

    override fun getItemStack(stack: ItemStack): ItemStack {
        stack.itemMeta<ItemMeta> {
            displayName(("&a称号 - &r" + getDisplay()).toComponent())
            lore(listOf(
                "&7&l --- &f&l称号介绍 &7&l--- ",
                "&7&l --- &f&l特殊效果 &7&l--- ",
                "&7&l --- &f&l解锁条件 &7&l--- ",
            ).toComponent())
        }
        return stack
    }

    override fun getDisplay(): String {
        return "&f[&e特殊&f]"
    }

    override fun unlockCondition(ecPlayer: ECPlayer): Boolean {
        return true
    }

    override fun afterUnlock(ecPlayer: ECPlayer) {

    }

}
