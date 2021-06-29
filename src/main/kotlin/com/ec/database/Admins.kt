package com.ec.database

import com.ec.database.enums.AdminStatus
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Admins: Table() {
    val id = varchar("id", 20)
    val name = varchar("name", 150)
    val apiKey = varchar("apiKey", 30).uniqueIndex()
    val status = enumerationByName("status", 30, AdminStatus::class)
    val createdAt = long("createdAt")
    val updatedAt = long("updatedAt")

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