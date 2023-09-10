package com.home.reader.ui

import com.home.reader.R
import com.home.reader.ui.login.state.ErrorState

val emptyLoginError = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.empty_login_error_msg
)

val emptyPasswordError = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.empty_password_error_msg
)