package com.home.reader.api

import android.content.Context
import coil.request.ImageRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.home.reader.api.dto.Credentials
import com.home.reader.api.dto.Issue
import com.home.reader.api.dto.Result
import com.home.reader.api.dto.Series
import com.home.reader.api.dto.Token
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.InputStreamReader
import java.lang.reflect.Type

class ApiProcessor(private val host: String, private val context: Context) {
    private val client = OkHttpClient()

    private companion object {
        val CONTENT_TYPE = "application/json".toMediaType()

        val SERIES_RESULT_TYPE: Type = TypeToken.getParameterized(
            List::class.java,
            Series::class.java
        ).type

        val ISSUES_RESULT_TYPE: Type = TypeToken.getParameterized(
            List::class.java,
            Issue::class.java
        ).type

        const val AUTH = "/customer/login"
        const val ALL_SERIES = "/comics/series"
        const val ISSUES_OF_SERIES = "/comics/series/%s/issues"
    }

    fun login(username: String, password: String): Result<Token> {
        val credentials = Credentials(username, password)
        val body = Gson().toJson(credentials).toRequestBody(CONTENT_TYPE)

        val request = Request.Builder()
            .post(body)
            .url("$host$AUTH")
            .build()

        return client.newCall(request).execute().toResult<Token>()
    }

    fun getSeries(token: String): Result<List<Series>> {
        val request = Request.Builder()
            .get()
            .url("$host$ALL_SERIES")
            .addAuthorizationHeader(token)
            .build()

        return client.newCall(request).execute().toResult(SERIES_RESULT_TYPE)
    }

    fun buildImageRequest(
        issueId: Long,
        token: String,
        page: Int = 0,
        size: String = "origin"
    ): ImageRequest {
        return ImageRequest.Builder(context = context)
            .data("$host/file/$issueId/$page?size=$size")
            .addHeader("Authorization", "Bearer $token")
            .build()
    }

    fun getIssues(seriesId: Long, token: String): Result<List<Issue>> {
        val path = ISSUES_OF_SERIES.format(seriesId)
        val request = Request.Builder()
            .get()
            .url("$host$path")
            .addAuthorizationHeader(token)
            .build()

        return client.newCall(request).execute().toResult(ISSUES_RESULT_TYPE)
    }

    private fun Request.Builder.addAuthorizationHeader(token: String): Request.Builder {
        return this.addHeader("Authorization", "Bearer $token")
    }

    private fun <T> Response.toResult(type: Type): Result<T> {
        if (this.code != 200) {
            return Result.failure(this.code)
        }

        val value = Gson().fromJson(InputStreamReader(this.body.byteStream()), type) as T
        return Result(value)
    }

    private inline fun <reified T> Response.toResult(): Result<T> {
        return this.toResult(TypeToken.get(T::class.java).type)
    }
}