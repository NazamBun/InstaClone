package com.nazam.instaclone.feature.auth.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.auth.domain.usecase.SignupUseCase
import com.nazam.instaclone.feature.auth.presentation.model.SignupUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SignupViewModel(
    private val dispatchers: AppDispatchers,
    private val signupUseCase: SignupUseCase
) {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState

    private val _events = MutableSharedFlow<AuthUiEvent>()
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
            emitError("Email et mot de passe requis")
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = signupUseCase.execute(
                state.email,
                state.password,
                state.displayName
            )

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthUiEvent.NavigateBack)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    emitError(error.message ?: "Erreur inconnue")
                }
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