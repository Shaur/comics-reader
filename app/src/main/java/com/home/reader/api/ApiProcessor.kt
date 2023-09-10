package com.home.reader.api

import android.content.Context
import coil.request.ImageRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.home.reader.api.dto.Credentials
import com.home.reader.api.dto.Issue
import com.home.reader.api.dto.Result
import com.home.reader.api.dto.Series
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.InputStreamReader
import java.lang.reflect.Type

class ApiProcessor(host: String, port: Int, private val context: Context) {

    private val baseUrl = "http://$host:$port"
    private val client = OkHttpClient()

    private companion object {
        val JSON_MEDIA_TYPE = "application/json".toMediaType()

        val TOKEN_TYPE: Type = TypeToken.getParameterized(
            Result::class.java,
            String::class.java
        ).type

        val SERIES_RESULT_TYPE: Type = TypeToken.getParameterized(
            List::class.java,
            Series::class.java
        ).type

        val ISSUES_RESULT_TYPE: Type = TypeToken.getParameterized(
            List::class.java,
            Issue::class.java
        ).type

        const val LOGIN_URL = "/auth/login"
        const val ALL_SERIES = "/comics/series"
        const val ISSUES_OF_SERIES = "/comics/series/%s/issues"
        const val READING_STATE_UPDATE = "/issue/%s"
    }

    fun login(login: String, password: String): Result<String> {
        val credentials = Credentials(login, password)
        val body = Gson().toJson(credentials).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .post(body)
            .url("$baseUrl$LOGIN_URL")
            .build()

        val response = client.newCall(request).execute()
        if (response.code != 200) {
            return Result.failure(response.code)
        }

        return Gson().fromJson(
            InputStreamReader(response.body?.byteStream()),
            TOKEN_TYPE
        )
    }

    fun getSeries(token: String): Result<List<Series>> {
        val request = Request.Builder()
            .get()
            .url("$baseUrl$ALL_SERIES")
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
            .data("$baseUrl/file/$issueId/$page?size=$size")
            .addHeader("Authorization", "Bearer $token")
            .build()
    }

    fun getIssues(seriesId: Long, token: String): Result<List<Issue>> {
        val path = ISSUES_OF_SERIES.format(seriesId)
        val request = Request.Builder()
            .get()
            .url("$baseUrl$path")
            .addAuthorizationHeader(token)
            .build()

        return client.newCall(request).execute().toResult(ISSUES_RESULT_TYPE)
    }

    fun isServerAvailable(): Boolean {
        val path = "/maintenance/ping"
        val request = Request.Builder()
            .get()
            .url("$baseUrl$path")
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.code == 200
        } catch (_: Exception) {
            false
        }
    }

    fun updateReadingState(issueId: Long, page: Int, token: String): Boolean {
        val url = READING_STATE_UPDATE.format(issueId)
        val body = Gson().toJson(mapOf("page" to page)).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .patch(body)
            .url(url)
            .addAuthorizationHeader(token)
            .build()

        val response = client.newCall(request).execute()
        return response.code == 200
    }

    private fun Request.Builder.addAuthorizationHeader(token: String): Request.Builder {
        return this.addHeader("Authorization", "Bearer $token")
    }

    private fun <T> Response.toResult(type: Type): Result<T> {
        if (this.code != 200) {
            return Result.failure(this.code)
        }

        val value = Gson().fromJson(InputStreamReader(this.body?.byteStream()), type) as T
        return Result(value)
    }

}