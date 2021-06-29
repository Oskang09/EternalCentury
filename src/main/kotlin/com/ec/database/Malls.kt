package com.ec.database

import com.ec.database.types.minecraft
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.Table

object Malls: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("playerId", 20)
    val playerName = varchar("playerName", 50)
    val material = enumerationByName("material", 50, Material::class)
    val nativeId = varchar("nativeId", 50).nullable()
    val item = minecraft("item", ItemStack::class.java)
    val amount = integer("amount")
    val price = double("price")
    val createdAt = long("createdAt")

    override val primaryKey = PrimaryKey(id)
}