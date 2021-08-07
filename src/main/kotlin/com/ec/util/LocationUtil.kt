package com.ec.util

import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

object LocationUtil {

    fun LivingEntity.handLocation(): Location {
        val location = this.eyeLocation.subtract(.0, .6, .0)
        val angle = location.yaw / 60
        val vector = Vector(
            cos(angle).toDouble(),
            0.0,
            sin(angle).toDouble()
        )
        return location.clone().subtract(vector.normalize().multiply(.45))
    }
}