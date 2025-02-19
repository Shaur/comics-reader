package com.home.reader.api

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.home.reader.persistence.entity.User
import com.home.reader.persistence.repository.UserRepository

class ApiHandler(
    context: Context,
    private val userRepository: UserRepository
) {

    private val processor = ApiProcessor("https://paper.webhop.me/api", context)

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
            Log.i(
                "Login request",
                """
                    Code: ${result.failReason?.code}
                    Reason: ${result.failReason?.text}
                """.trimIndent()
            )

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

}