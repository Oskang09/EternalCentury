package com.ec.api.endpoints.issues

import com.ec.api.EndpointAPI
import com.ec.api.ApiException
import com.ec.api.ResultResponse
import com.ec.database.AuditLog
import com.ec.database.Issues
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class MarkIssueResolved : EndpointAPI() {
    override val method = HandlerType.PUT
    override val path = "/issue/:id"
    override val handler = Handler { ctx ->
        ctx.register(AuditLog::class.java, AuditLog("Mark Issue Resolved"))

        val id = ctx.pathParam("id")
        val updateCount = transaction {
            Issues.update({ Issues.id eq id }) { iss ->
                iss[resolved] = true
            }
        }

        when (updateCount) {
            1 -> ctx.json(ResultResponse(result = "issues mark resolved"))
            else -> throw ApiException(400, "fail to resolve issues")
        }

    }
}