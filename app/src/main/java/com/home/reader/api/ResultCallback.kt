package com.home.reader.api

interface ResultCallback<T> {

    fun onSuccess(t: T)

    fun onFail(code: Int, message: String?)

}