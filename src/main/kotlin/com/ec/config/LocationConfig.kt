package com.ec.config

import org.bukkit.Bukkit
import org.bukkit.Location

data class LocationConfig(
    var world: String = "",
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0.0F,
    var pitch: Float = 0.0F,
) {
    constructor(location: Location) : this(location.world!!.name, location.x, location.y, location.z, location.yaw, location.pitch)

    val location: Location
        get() = Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
}