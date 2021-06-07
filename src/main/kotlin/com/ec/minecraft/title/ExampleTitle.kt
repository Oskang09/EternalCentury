package com.ec.minecraft.title

import com.ec.extension.GlobalManager
import com.ec.model.player.ECPlayer
import com.ec.extension.title.TitleAPI
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ExampleTitle: TitleAPI("some_id", 0) {
    override fun initialize(globalManager: GlobalManager) {

    }

    override fun getItemStack(stack: ItemStack): ItemStack {
        stack.itemMeta<ItemMeta> {
            setDisplayName(("&a称号 - &r" + getDisplay()).colorize())
            lore = listOf(
                "&7 --- 称号介绍 --- ",
                "&7 --- 特殊效果 --- ",
                "&7 --- 解锁条件 --- ",
            ).colorize()
        }
        return stack
    }

    override fun getDisplay(): String {
        return ""
    }

    override fun unlockCondition(ecPlayer: ECPlayer): Boolean {
        return true
    }

}
