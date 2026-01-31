package com.nazam.instaclone.feature.auth.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LoginUseCase
import com.nazam.instaclone.feature.auth.presentation.model.LoginUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.error_email_password_required
import instaclone.composeapp.generated.resources.error_unknown
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val dispatchers: AppDispatchers,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _events = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthUiEvent> = _events

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun checkSession() {
        scope.launch {
            val user = withContext(dispatchers.default) { getCurrentUserUseCase.execute() }
            if (user != null) navigateAfterLogin()
        }
    }

    fun login() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update {
                it.copy(error = UiText.Resource(Res.string.error_email_password_required))
            }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = withContext(dispatchers.default) {
                loginUseCase.execute(state.email, state.password)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    navigateAfterLogin()
                }
                .onFailure { error ->
                    val msg = error.message?.takeIf { it.isNotBlank() }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = msg?.let { UiText.DynamicString(it) }
                                ?: UiText.Resource(Res.string.error_unknown)
                        )
                    }
                }
        }
    }

    fun goToSignup() {
        _events.tryEmit(AuthUiEvent.Navigate(Screen.Signup))
    }

    private fun navigateAfterLogin() {
        val target = NavigationStore.consumeAfterLogin() ?: Screen.Home
        _events.tryEmit(AuthUiEvent.Navigate(target))
    }

    fun clear() {
        job.cancel()
    }
}