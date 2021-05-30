package com.ec.minecraft.title

import com.ec.extension.GlobalManager
import com.ec.extension.player.ECPlayer
import com.ec.extension.title.TitleAPI
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ExampleTitle: TitleAPI("", 0) {
    override fun initialize(globalManager: GlobalManager) {

    }

    override fun uiDisplay(stack: ItemStack): ItemStack {
        stack.itemMeta<ItemMeta> {
            setDisplayName("§a称号 - §r" + display())
            lore = listOf(
                "§7 --- 称号介绍 --- ",
                "§7 --- 特殊效果 --- ",
                "§7 --- 解锁条件 --- ",
            )
        }
        return stack
    }

    override fun display(): String {
        return ""
    }

    override fun unlockCondition(ecPlayer: ECPlayer): Boolean {
        return true
    }

}
