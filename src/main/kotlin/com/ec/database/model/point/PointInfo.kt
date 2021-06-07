package com.ec.database.model.point

data class PointInfo(
    val points: MutableMap<String, PointDetail> = mutableMapOf()
)
