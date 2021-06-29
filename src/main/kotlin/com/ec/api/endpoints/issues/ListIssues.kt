package com.ec.api.endpoints.issues

import com.ec.api.EndpointAPI
import com.ec.api.PaginationResponse
import com.ec.database.Issues
import com.ec.util.ModelUtil.toJSON
import com.ec.util.QueryUtil.iterator
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ListIssues: EndpointAPI() {
    override val method = HandlerType.GET
    override val path = "/issues"
    override val handler = Handler { ctx ->
        transaction {
            val query = Issues.selectAll()
            var cursor = ""
            ctx.queryParamMap().forEach { (key, values) ->
                when (key) {
                    "id" -> query.andWhere { Issues.id inList values }
                    "title" -> query.andWhere { Issues.title like values[0] }
                    "resolved" -> query.andWhere { Issues.resolved eq values[0].toBooleanStrict() }
                    "cursor" -> cursor = values[0]
                }
            }

            val response = query.iterator(Issues.id, 20, cursor)
            ctx.json(PaginationResponse(
                meta = response.cursor,
                result = response.items.toJSON(),
            ))
        }
    }
}