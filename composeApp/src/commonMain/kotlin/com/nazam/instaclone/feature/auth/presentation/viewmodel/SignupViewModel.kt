package com.nazam.instaclone.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.feature.auth.data.error.AuthErrorMapper
import com.nazam.instaclone.feature.auth.domain.usecase.SignupUseCase
import com.nazam.instaclone.feature.auth.presentation.model.SignupUiState
import com.nazam.instaclone.feature.auth.presentation.validator.AuthValidator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupViewModel(
    private val dispatchers: AppDispatchers,
    private val signupUseCase: SignupUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthUiEvent> = _events.asSharedFlow()

    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun onConfirmPasswordChanged(value: String) = _uiState.update { it.copy(confirmPassword = value, error = null) }
    fun onDisplayNameChanged(value: String) = _uiState.update { it.copy(displayName = value, error = null) }

    fun signup() {
        val state = _uiState.value
        AuthValidator.validateSignup(state.email, state.password, state.confirmPassword)?.let { error ->
            _uiState.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = withContext(dispatchers.io) {
                signupUseCase.execute(state.email, state.password, state.displayName)
            }
            result
                .onSuccess { user ->
                    sessionManager.setUser(user)
                    _uiState.update { it.copy(isLoading = false, isSignedUp = true) }
                    val target = NavigationStore.consumeAfterLogin() ?: Screen.Home
                    _events.tryEmit(AuthUiEvent.Navigate(target))
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = AuthErrorMapper.map(error))
                    }
                }
        }
    }

    fun onBackClicked() {
        _events.tryEmit(AuthUiEvent.NavigateBack)
    }
}
