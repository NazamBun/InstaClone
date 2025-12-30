package com.nazam.instaclone.feature.auth.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LoginUseCase
import com.nazam.instaclone.feature.auth.presentation.model.LoginUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ✅ ViewModel KMP pur
 * - StateFlow = état
 * - SharedFlow = events
 */
class LoginViewModel(
    private val dispatchers: AppDispatchers,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _events = MutableSharedFlow<AuthUiEvent>()
    val events: SharedFlow<AuthUiEvent> = _events

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            emitError("Email et mot de passe requis")
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = loginUseCase.execute(state.email, state.password)

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthUiEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    emitError(error.message ?: "Erreur inconnue")
                }
        }
    }

    fun goToSignup() {
        scope.launch {
            _events.emit(AuthUiEvent.NavigateToSignup)
        }
    }

    private fun emitError(message: String) {
        scope.launch {
            _events.emit(AuthUiEvent.ShowError(message))
        }
    }

    fun clear() {
        job.cancel()
    }
}