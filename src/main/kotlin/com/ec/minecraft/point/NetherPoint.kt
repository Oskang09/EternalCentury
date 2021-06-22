package com.ec.minecraft.point

import com.ec.database.model.point.PointDetail
import com.ec.manager.point.PointAPI
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class NetherPoint: PointAPI("nether") {
    override fun getItemStack(point: PointDetail): ItemStack {
        val stack = ItemStack(Material.OBSIDIAN)
        stack.itemMeta<ItemMeta> {
            setDisplayName("&c地狱点数".colorize())
            lore = arrayListOf(
                "&7&l --- &f&l点数介绍 &7&l--- ",
                "&f此点数只有在末地内才能通过物品兑换",
                "&f拥有点数后能兑换一些地狱专有物品",
                "&7&l --- &f&l点数咨询 &7&l--- ",
                "&f所有点数 - &e${point.total}",
                "&f剩余点数 - &e${point.balance}",
                "&f点数阶级 - &e${point.grade}"
            ).colorize()
        }
        return stack
    }

    override fun getGrade(point: PointDetail): Int {
        return 1
    }

}