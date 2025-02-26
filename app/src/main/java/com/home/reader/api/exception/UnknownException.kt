package com.home.reader.api.exception

class UnknownException(code: Int?, text: String?) :
    RuntimeException("Unknown error code: $code, reason: $text")
