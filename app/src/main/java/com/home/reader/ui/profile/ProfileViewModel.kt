package com.home.reader.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.reader.persistence.entity.User
import com.home.reader.persistence.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    var loginState = mutableStateOf<User?>(null)

    init {
        viewModelScope.launch {
            loginState.value = userRepository.get()
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.delete()
            loginState.value = null
        }
    }
}