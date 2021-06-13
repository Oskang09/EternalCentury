package com.ec.api

data class ErrorResponse(
    val error: String? = null,
    val message: String,
)