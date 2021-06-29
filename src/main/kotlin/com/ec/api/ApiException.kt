package com.ec.api

class ApiException(val status: Int = 500, val error: String = ""): Exception(error)