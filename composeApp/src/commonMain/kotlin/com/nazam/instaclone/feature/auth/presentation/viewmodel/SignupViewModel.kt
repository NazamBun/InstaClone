package com.nazam.instaclone.feature.auth.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.domain.usecase.SignupUseCase
import com.nazam.instaclone.feature.auth.presentation.model.SignupUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel KMP pur.
 * - UiState = état durable
 * - Events = actions one-shot (navigation)
 */
class SignupViewModel(
    private val dispatchers: AppDispatchers,
    private val signupUseCase: SignupUseCase
) {
    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState

    private val _events = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthUiEvent> = _events

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onDisplayNameChanged(value: String) {
        _uiState.update { it.copy(displayName = value, errorMessage = null) }
    }

    fun signup() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email et mot de passe requis") }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = withContext(dispatchers.default) {
                signupUseCase.execute(state.email, state.password, state.displayName)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSignedUp = true) }

                    // Important : après signup, on va où il faut.
                    // Home par défaut, ou CreatePost si demandé avant.
                    val target = NavigationStore.consumeAfterLogin() ?: Screen.Home
                    _events.tryEmit(AuthUiEvent.Navigate(target))
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erreur inconnue"
                        )
                    }
                }
        }
    }

    fun clear() {
        job.cancel()
    }
}