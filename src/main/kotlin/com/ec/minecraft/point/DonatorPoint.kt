package com.ec.minecraft.point

import com.ec.database.model.point.PointDetail
import com.ec.extension.point.PointAPI

class DonatorPoint: PointAPI("donator") {

    override fun getGrade(point: PointDetail): Int {
        return 0
    }

}