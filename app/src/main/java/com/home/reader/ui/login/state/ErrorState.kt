package com.home.reader.ui.login.state

import androidx.annotation.StringRes
import com.home.reader.R

data class ErrorState(
    val hasError: Boolean = false,
    @StringRes val errorMessageStringResource: Int = R.string.empty_string
)
