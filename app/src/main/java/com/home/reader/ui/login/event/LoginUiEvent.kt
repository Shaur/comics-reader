package com.home.reader.ui.login.event

sealed class LoginUiEvent {

    data class LoginChangeEvent(val input: String): LoginUiEvent()

    data class PasswordChangeEvent(val input: String): LoginUiEvent()

    object Submit : LoginUiEvent()
}