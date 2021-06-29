package com.ec.api.endpoints.admins

import com.ec.api.EndpointAPI
import com.ec.api.ApiException
import com.ec.api.ResultResponse
import com.ec.database.Admins
import com.ec.database.AuditLog
import com.ec.database.enums.AdminStatus
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

class PatchAdmin: EndpointAPI() {
    override val method = HandlerType.PATCH
    override val path = "/admin/:id"

    data class Request(
        val name: String,
        val status: AdminStatus,
    )

    override val handler = Handler { ctx ->
        ctx.register(AuditLog::class.java, AuditLog("Toggle Admin Expired"))

        val request = ctx.bodyAsClass(Request::class.java)
        val id = ctx.pathParam("id")
        val updateCount = transaction {
            Admins.update({ Admins.id eq id }) { admin ->
                admin[status] = request.status
                admin[name] = request.name
                admin[updatedAt] = Instant.now().epochSecond
            }
        }

        when (updateCount) {
            1 -> ctx.json(ResultResponse(result = "admin patch successfully"))
            else -> throw ApiException(400, "fail to patch admin")
        }
    }
}