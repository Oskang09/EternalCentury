package com.ec.api.endpoints.announcement

import com.ec.api.EndpointAPI
import com.ec.api.PaginationResponse
import com.ec.database.Announcements
import com.ec.util.ModelUtil.toJSON
import com.ec.util.QueryUtil.iterator
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ListAnnouncements: EndpointAPI() {
    override val method = HandlerType.GET
    override val path = "/announcements"
    override val handler = Handler { ctx ->
        transaction {
            val query = Announcements.selectAll()
            var cursor = ""
            ctx.queryParamMap().forEach { (key, values) ->
                when (key) {
                    "id" -> query.andWhere { Announcements.id inList values }
                    "title" -> query.andWhere { Announcements.title like values[0] }
                    "content" -> query.andWhere { Announcements.content like values[0] }
                    "cursor" -> cursor = values[0]
                }
            }

            val response = query.iterator(Announcements.id, 20, cursor)
            ctx.json(PaginationResponse(
                meta = response.cursor,
                result = response.items.toJSON(),
            ))
        }
    }
}