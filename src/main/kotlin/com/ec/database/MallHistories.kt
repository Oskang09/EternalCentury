package com.ec.database

import com.ec.database.types.enum
import com.ec.database.types.minecraft
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.Table

object MallHistories: Table() {
    val id = varchar("id", 20)
    val buyerId = varchar("buyer_id", 20)
    val sellerId = varchar("seller_id", 20)
    val material = enum<Material>("material")
    val name = varchar("name", 128)
    val item = minecraft("item", ItemStack::class.java)
    val amount = integer("amount")
    val price = integer("price")

    override val primaryKey = PrimaryKey(id)
}