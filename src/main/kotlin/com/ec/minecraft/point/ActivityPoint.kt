package com.ec.minecraft.point

import com.ec.database.model.point.PointDetail
import com.ec.manager.point.PointAPI
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ActivityPoint: PointAPI("activity") {

    override fun getItemStack(point: PointDetail): ItemStack {
        val stack = ItemStack(Material.ZOMBIE_HEAD)
        stack.itemMeta<ItemMeta> {
            setDisplayName("&f活动点数".colorize())
            lore = arrayListOf(
                "&7&l --- &f&l点数介绍 &7&l--- ",
                "&f此点数只有参与活动才能获得哟",
                "&f拥有点数后能兑换一些罕见珍贵的物品",
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