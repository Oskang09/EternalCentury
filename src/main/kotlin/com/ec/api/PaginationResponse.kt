package com.ec.api

class PaginationResponse<T, M>(
    val meta: M,
    val result: T,
)