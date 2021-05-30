package com.ec.minecraft.point

import com.ec.extension.point.PointAPI

class DonatorPoint: PointAPI("donator") {

    override fun getGrade(point: Int): Int {
        return 0
    }

}