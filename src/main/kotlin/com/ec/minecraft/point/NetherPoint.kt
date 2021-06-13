package com.ec.minecraft.point

import com.ec.database.model.point.PointDetail
import com.ec.extension.point.PointAPI
import org.bukkit.inventory.ItemStack

class NetherPoint: PointAPI("nether") {
    override fun getItemStack(point: PointDetail): ItemStack {
        TODO("Not yet implemented")
    }

    override fun getGrade(point: PointDetail): Int {
        return 1
    }

}