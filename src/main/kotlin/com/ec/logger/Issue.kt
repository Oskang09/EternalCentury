package com.ec.logger

data class Issue(
    val id: String,
    val title: String,
    val message: String,
    val stack: List<String>,
    val timestamp: String
)