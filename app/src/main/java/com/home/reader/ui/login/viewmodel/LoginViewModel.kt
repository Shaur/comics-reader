package com.home.reader.ui.login.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.reader.api.ApiHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val api: ApiHandler
) : ViewModel() {

    val loginError = mutableStateOf<Boolean?>(null)

    init {
        viewModelScope.launch { api.initToken() }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isLogin = api.login(username, password)
            loginError.value = !isLogin
        }
    }
}