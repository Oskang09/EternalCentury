package com.ec.util

import org.jetbrains.exposed.sql.*

object QueryUtil {

    data class IteratorResult(
        var cursor: String = "",
        var items: List<ResultRow> = listOf()
    )

    fun Query.iterator(key: Column<String>, limit: Int, cursor: String): IteratorResult {
        val result = IteratorResult()
        val query = this.limit(limit + 1)
        if (cursor != "") {
            query.andWhere { key lessEq cursor }
        }
        var lists = query.orderBy(key to SortOrder.DESC)
            .toList()
        if (lists.size == limit + 1) {
            result.cursor = lists.last()[key]
            lists = lists.dropLast(1)
        }

        result.items = lists
        return result
    }

}