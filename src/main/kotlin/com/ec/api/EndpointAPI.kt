package com.ec.api

import com.ec.manager.GlobalManager
import io.javalin.http.Handler
import io.javalin.http.HandlerType

abstract class EndpointAPI {
    lateinit var globalManager: GlobalManager

    abstract val method: HandlerType
    abstract val path: String
    abstract val handler: Handler
}