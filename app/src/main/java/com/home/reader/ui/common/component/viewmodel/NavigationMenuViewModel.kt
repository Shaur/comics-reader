package com.home.reader.ui.common.component.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.reader.persistence.entity.User
import com.home.reader.persistence.repository.UserRepository
import kotlinx.coroutines.launch

class NavigationMenuViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var loginState = mutableStateOf<User?>(null)

    init {
        viewModelScope.launch {
            loginState.value = userRepository.get()
        }
    }
}