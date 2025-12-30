package com.nazam.instaclone.feature.auth.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LoginUseCase
import com.nazam.instaclone.feature.auth.presentation.model.LoginUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ✅ ViewModel KMP pur
 * - Pas de KoinComponent
 * - Dépendances injectées par constructeur
 * - Compatible Android & iOS
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

    /** Quand l’email change */
    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    /** Quand le mot de passe change */
    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    /** Clique sur "Se connecter" */
    fun login() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email et mot de passe requis") }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = withContext(dispatchers.default) {
                loginUseCase.execute(state.email, state.password)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erreur de connexion"
                        )
                    }
                }
        }
    }

    /** Vérifie si l’utilisateur est déjà connecté */
    fun checkSession() {
        scope.launch {
            val user = withContext(dispatchers.default) {
                getCurrentUserUseCase.execute()
            }

            if (user != null) {
                _uiState.update { it.copy(isLoggedIn = true) }
            }
        }
    }

    /** Stoppe les coroutines */
    fun clear() {
        job.cancel()
    }
}