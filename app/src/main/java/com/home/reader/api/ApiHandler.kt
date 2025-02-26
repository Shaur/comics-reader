package com.home.reader.api

import androidx.compose.runtime.mutableStateOf
import com.home.reader.api.dto.Issue
import com.home.reader.api.dto.Series
import com.home.reader.api.dto.Result
import com.home.reader.api.exception.TokenUpdateFailedException
import com.home.reader.api.exception.Unauthorized
import com.home.reader.api.exception.UnknownException
import com.home.reader.persistence.entity.User
import com.home.reader.persistence.repository.UserRepository
import okhttp3.Response

class ApiHandler(private val userRepository: UserRepository) {

    private val processor = ApiProcessor("https://paper.webhop.me/api")

    private val tokenState = mutableStateOf<String?>(null)

    suspend fun initToken() {
        if (tokenState.value == null) {
            val user = userRepository.get()
            tokenState.value = user?.token
        }
    }

    suspend fun login(username: String, password: String): Boolean {
        val result = processor.login(username, password)
        if (!result.isSuccess()) {
            return false
        }

        val existsUser = userRepository.get()
        if (existsUser == null) {
            val user = User(
                username = username,
                password = password,
                token = result.value?.token
            )
            userRepository.insert(user)
        } else {
            val user = existsUser.copy(
                username = username,
                password = password,
                token = result.value?.token
            )
            userRepository.update(user)
        }

        tokenState.value = result.value?.token
        return true
    }

    private suspend fun updateToken(user: User): String {
        val result = processor.login(user.username, user.password)
        if (result.isSuccess()) {
            userRepository.update(user.copy(token = result.value!!.token))
            return result.value.token
        }

        throw TokenUpdateFailedException()
    }

    suspend fun getSeries(): List<Series> {
        return withToken { processor.getSeries(it) }
    }

    suspend fun getIssues(seriesId: Long): List<Issue> {
        return withToken { processor.getIssues(seriesId, it) }
    }

    suspend fun updateProgress(issueId: Long, currentPage: Int) {
        withToken { processor.updateProgress(issueId, currentPage, it) }
    }

    fun buildImageUrl(url: String): String = processor.buildImageUrl(url)

    private suspend fun <T> withToken(action: (String) -> Result<T>): T {
        val user = userRepository.get() ?: throw Unauthorized()
        val token = user.token ?: throw Unauthorized()
        val result = action(token)

        if (result.isSuccess()) {
            return result.value!!
        }

        if (result.failReason?.code == 403) {
            val updatedToken = updateToken(user)
            val attemptResult = action(updatedToken)
            if (attemptResult.isSuccess()) {
                return attemptResult.value!!
            }

            if (!attemptResult.isSuccess()) {
                throw UnknownException(
                    attemptResult.failReason?.code,
                    attemptResult.failReason?.text
                )
            }
        }

        throw UnknownException(result.failReason?.code, result.failReason?.text)

    }

}