package com.ec.api

data class ActionLog(
    val url: String,
    val requestBody: String,
    val responseBody: String,
    val statusCode: Int,
    val headers: Map<String, String>
)