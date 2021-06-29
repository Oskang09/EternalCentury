package com.ec.database

import com.ec.database.types.minecraft
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.Table

object MallHistories: Table() {
    val id = varchar("id", 20)
    val buyerId = varchar("buyer_id", 20)
    val buyerName = varchar("buyer_name", 50)
    val sellerId = varchar("seller_id", 20)
    val sellerName = varchar("seller_name", 50)
    val material = enumerationByName("material", 50, Material::class)
    val nativeId = varchar("native_id", 50).nullable()
    val item = minecraft("item", ItemStack::class.java)
    val amount = integer("amount")
    val price = double("price")
    val historyAt = long("history_at")

    override val primaryKey = PrimaryKey(id)
}