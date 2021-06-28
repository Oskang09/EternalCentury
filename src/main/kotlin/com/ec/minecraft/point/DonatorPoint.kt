package com.ec.minecraft.point

import com.ec.database.model.point.PointDetail
import com.ec.manager.point.PointAPI
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class DonatorPoint: PointAPI("donator") {
    override fun getItemStack(point: PointDetail): ItemStack {
        val stack = ItemStack(Material.NETHER_STAR)
        stack.itemMeta<ItemMeta> {
            displayName("&e捐献点数".toComponent())
            lore(arrayListOf(
                "&7&l --- &f&l点数介绍 &7&l--- ",
                "&f此点数只有通过赞助和一些特殊活动才能获得",
                "&f能兑换一些较为难获得的物品",
                "&7&l --- &f&l点数咨询 &7&l--- ",
                "&f所有点数 - &e${point.total}",
                "&f剩余点数 - &e${point.balance}",
                "&f点数阶级 - &e${point.grade}"
            ).toComponent())
        }
        return stack
    }

    override fun getGrade(point: PointDetail): Int {
        return 1
    }

}