package com.ec.minecraft.point

import com.ec.database.model.point.PointDetail
import com.ec.extension.point.PointAPI
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class SeasonPoint: PointAPI("season") {
    override fun getItemStack(point: PointDetail): ItemStack {
        val stack = ItemStack(Material.GRASS_BLOCK)
        stack.itemMeta<ItemMeta> {
            setDisplayName("&b赛季点数".colorize())
            lore = arrayListOf(
                "&7&l --- &f&l点数介绍 &7&l--- ",
                "&f此点数能在每次的赛季世界获得",
                "&f每个赛季的点数是通用的",
                "&f能够兑换赛季专有物品",
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