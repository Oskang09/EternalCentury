package com.ec.api.endpoints.admins

import com.ec.api.EndpointAPI
import com.ec.api.ResultResponse
import com.ec.database.Admins
import com.ec.database.AuditLog
import com.ec.database.enums.AdminStatus
import com.ec.util.ModelUtil.toJSON
import com.ec.util.StringUtil.generateUniqueID
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class CreateAdmin: EndpointAPI() {
    override val method = HandlerType.POST
    override val path = "/admin"

    data class Request(
        val name: String,
    )

    private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override val handler = Handler { ctx ->
        ctx.register(AuditLog::class.java, AuditLog("Create Admin"))

        val request = ctx.bodyAsClass(Request::class.java)
        transaction {
            val res = Admins.insert {
                it[id] = "".generateUniqueID()
                it[name] = request.name
                it[apiKey] = (1..30).map { charPool.random() }.joinToString("")
                it[status] = AdminStatus.ACTIVE
                it[createdAt] = Instant.now().epochSecond
                it[updatedAt] = Instant.now().epochSecond
            }
            ctx.json(ResultResponse(res.resultedValues!![0].toJSON()))
        }
    }
}