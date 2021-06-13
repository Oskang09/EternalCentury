package com.ec.api

import io.javalin.http.Handler
import io.javalin.http.HandlerType

abstract class EndpointAPI {
    abstract val method: HandlerType
    abstract val path: String
    abstract val handler: Handler
}