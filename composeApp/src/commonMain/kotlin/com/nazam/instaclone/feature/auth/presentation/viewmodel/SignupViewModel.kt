package com.nazam.instaclone.feature.auth.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.auth.domain.usecase.SignupUseCase
import com.nazam.instaclone.feature.auth.presentation.model.SignupUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ✅ ViewModel KMP "pur"
 * - pas de KoinComponent
 * - dépendances via constructeur (SOLID)
 */
class SignupViewModel(
    private val dispatchers: AppDispatchers,
    private val signupUseCase: SignupUseCase
) {
    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState

    /** ✅ Quand l’email change */
    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    /** ✅ Quand le mot de passe change */
    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    /** ✅ Quand le pseudo change */
    fun onDisplayNameChanged(newName: String) {
        _uiState.update { it.copy(displayName = newName, errorMessage = null) }
    }

    /** ✅ Clique sur "Créer un compte" */
    fun signup() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val displayName = _uiState.value.displayName.trim()

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email et mot de passe requis") }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = withContext(dispatchers.default) {
                signupUseCase.execute(email, password, displayName)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSignedUp = true) }
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

    /** ✅ Stoppe les coroutines */
    fun clear() {
        job.cancel()
    }

    // ✅ Compat
    fun onClear() = clear()
}