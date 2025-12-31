package com.nazam.instaclone.feature.auth.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LoginUseCase
import com.nazam.instaclone.feature.auth.presentation.model.LoginUiState
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
 * - StateFlow = état durable
 * - SharedFlow = events one-shot (navigation)
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

    private val _events = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthUiEvent> = _events

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    /**
     * Si déjà connecté -> on navigue vers la bonne destination
     * (Home par défaut, ou CreatePost si demandé avant).
     */
    fun checkSession() {
        scope.launch {
            val user = withContext(dispatchers.default) { getCurrentUserUseCase.execute() }
            if (user != null) {
                navigateAfterLogin()
            }
        }
    }

    fun login() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email et mot de passe requis") }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = withContext(dispatchers.default) {
                loginUseCase.execute(state.email, state.password)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    navigateAfterLogin()
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