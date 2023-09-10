package com.home.reader.ui.login.state

data class LoginState(
    val login: String = "",
    val password: String = "",
    val errorState: LoginErrorState = LoginErrorState(),
    val isLoginSuccessful: Boolean = false,
    val inProgress: Boolean = false
)

data class LoginErrorState(
    val loginErrorState: ErrorState = ErrorState(),
    val passwordErrorState: ErrorState = ErrorState()
)