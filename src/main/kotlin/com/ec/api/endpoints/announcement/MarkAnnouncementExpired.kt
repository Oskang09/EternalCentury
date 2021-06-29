package com.ec.api.endpoints.announcement

import com.ec.api.EndpointAPI
import com.ec.api.ApiException
import com.ec.api.ResultResponse
import com.ec.database.Announcements
import com.ec.database.AuditLog
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class MarkAnnouncementExpired: EndpointAPI() {
    override val method = HandlerType.DELETE
    override val path = "/announcement/:id"

    override val handler = Handler { ctx ->
        ctx.register(AuditLog::class.java, AuditLog("Mark Annoucement Expired"))

        val id = ctx.pathParam("id")
        val updateCount = transaction {
            Announcements.update({ Announcements.id eq id }) { mail ->
                mail[isExpired] = true
            }
        }

        when (updateCount) {
            1 -> ctx.json(ResultResponse(result = "announcement mark expired successfully"))
            else -> throw ApiException(400, "fail to expired annoucement")
        }

    }
}