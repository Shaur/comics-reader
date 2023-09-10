package com.home.reader.ui.login.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.reader.api.ApiProcessor
import com.home.reader.persistence.entity.Credentials
import com.home.reader.persistence.repository.CredentialsRepository
import com.home.reader.ui.common.GlobalState
import com.home.reader.ui.emptyLoginError
import com.home.reader.ui.emptyPasswordError
import com.home.reader.ui.login.event.LoginUiEvent
import com.home.reader.ui.login.state.ErrorState
import com.home.reader.ui.login.state.LoginErrorState
import com.home.reader.ui.login.state.LoginState
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class LoginViewModel(
    private val repository: CredentialsRepository,
    private val api: ApiProcessor,
    private val globalState: MutableState<GlobalState>
) : ViewModel() {

    var loginState = mutableStateOf(LoginState())
        private set

    init {
        Thread {
            val isServiceAvailable = api.isServerAvailable()
            globalState.value = globalState.value.copy(
                serviceAvailable = isServiceAvailable
            )

            viewModelScope.launch {
                repository.findFirstToken().collect {
                    loginState.value = loginState.value.copy(
                        isLoginSuccessful = (it != null)
                    )

                    globalState.value = globalState.value.copy(token = it)
                }
            }
        }.start()
    }

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.LoginChangeEvent -> {
                loginState.value = loginState.value.copy(
                    login = event.input,
                    errorState = loginState.value.errorState.copy(
                        loginErrorState = if (event.input.isNotBlank()) ErrorState() else emptyLoginError
                    )
                )
            }

            is LoginUiEvent.PasswordChangeEvent -> {
                loginState.value = loginState.value.copy(
                    password = event.input,
                    errorState = loginState.value.errorState.copy(
                        passwordErrorState = if (event.input.isNotBlank()) ErrorState() else emptyPasswordError
                    )
                )
            }

            LoginUiEvent.Submit -> {
                val inputsValidated = validateInputs()
                if (inputsValidated) {
                    loginState.value = loginState.value.copy(inProgress = true)

                    Thread {
                        val result = api.login(loginState.value.login, loginState.value.password)
                        val success = result.isSuccess()
                        if (success) {
                            val credentials = Credentials(
                                login = loginState.value.login,
                                password = loginState.value.password,
                                token = result.value!!
                            )

                            globalState.value = globalState.value.copy(
                                token = result.value,
                                serviceAvailable = true
                            )
                            viewModelScope.launch { repository.insert(credentials) }
                        }

                        loginState.value = loginState.value.copy(
                            isLoginSuccessful = success,
                            inProgress = false
                        )
                    }.start()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val login = loginState.value.login.trim()
        val password = loginState.value.password

        return when {
            login.isEmpty() -> {
                loginState.value = loginState.value.copy(
                    errorState = LoginErrorState(
                        loginErrorState = emptyLoginError
                    )
                )
                false
            }

            password.isEmpty() -> {
                loginState.value = loginState.value.copy(
                    errorState = LoginErrorState(
                        passwordErrorState = emptyPasswordError
                    )
                )
                false
            }

            else -> {
                loginState.value = loginState.value.copy(errorState = LoginErrorState())
                true
            }
        }
    }

}