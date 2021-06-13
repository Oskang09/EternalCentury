package com.ec.api.endpoints

import com.ec.api.EndpointAPI
import com.ec.api.ErrorResponse
import com.ec.api.ResultResponse
import com.ec.database.Announcements
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

class MarkAnnouncementExpired: EndpointAPI() {
    override val method: HandlerType = HandlerType.DELETE
    override val path: String = "/announcement/:id"

    override val handler: Handler = Handler { ctx ->
        val id = ctx.pathParam("id")
        val updateCount = transaction {
            Announcements.update({ Announcements.id eq id }) { mail ->
                mail[isExpired] = true
            }
        }
        if (updateCount != 1) {
            ctx.json(ErrorResponse(
                message = ""
            ))
            return@Handler
        }
        ctx.json(ResultResponse(result = "announcement mark expired successfully"))
    }
}