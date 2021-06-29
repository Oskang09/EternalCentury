package com.ec.database

import com.ec.database.enums.AdminStatus
import com.ec.database.types.enum
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Admins: Table() {
    val id = varchar("id", 20)
    val name = varchar("name", 150)
    val apiKey = varchar("api_key", 30).uniqueIndex()
    val status = enumerationByName("status", 30, AdminStatus::class)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}

data class Admin(
    val id: String,
    val name: String,
    val apiKey: String,
    val createdAt: Long,
    val updatedAt: Long,
) {

    constructor(row: ResultRow): this(
        row[Admins.id],
        row[Admins.name],
        row[Admins.apiKey],
        row[Admins.createdAt],
        row[Admins.updatedAt]
    )

}