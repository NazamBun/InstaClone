package com.nazam.instaclone.feature.auth.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.SignupUseCase
import com.nazam.instaclone.feature.auth.presentation.model.SignupUiState
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

class SignupViewModel(
    private val dispatchers: AppDispatchers,
    private val signupUseCase: SignupUseCase,
    private val sessionManager: SessionManager
) {
    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState

    private val _events = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthUiEvent> = _events

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun onDisplayNameChanged(value: String) {
        _uiState.update { it.copy(displayName = value, error = null) }
    }

    fun signup() {
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
                signupUseCase.execute(state.email, state.password, state.displayName)
            }

            result
                .onSuccess { user ->
                    // ✅ session connectée
                    sessionManager.setUser(user)

                    _uiState.update { it.copy(isLoading = false, isSignedUp = true) }
                    val target = NavigationStore.consumeAfterLogin() ?: Screen.Home
                    _events.tryEmit(AuthUiEvent.Navigate(target))
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

    fun clear() {
        job.cancel()
    }

    fun onBackClicked() {
        _events.tryEmit(AuthUiEvent.NavigateBack)
    }
}