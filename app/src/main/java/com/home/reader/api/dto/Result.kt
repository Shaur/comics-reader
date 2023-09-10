package com.home.reader.api.dto

data class Result<T>(
    val value: T?,
    val type: Type = Type.SUCCESS,
    val failReason: FailReason? = null
) {
    enum class Type {
        SUCCESS,
        FAIL
    }

    data class FailReason(val code: Int, val text: String? = null)

    companion object {
        fun <T> failure(code: Int, reason: String? = null): Result<T> {
            return Result(null, Type.FAIL, FailReason(code, reason))
        }
    }

    fun isSuccess(): Boolean = (value != null)


}
