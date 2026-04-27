package com.nazam.instaclone.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.feature.auth.data.error.AuthErrorMapper
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LoginUseCase
import com.nazam.instaclone.feature.auth.presentation.model.LoginUiState
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

class LoginViewModel(
    private val dispatchers: AppDispatchers,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthUiEvent> = _events.asSharedFlow()

    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value, error = null) }

    fun checkSession() {
        viewModelScope.launch {
            val user = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
            if (user != null) {
                sessionManager.setUser(user)
                navigateAfterLogin()
            }
        }
    }

    fun login() {
        val state = _uiState.value
        AuthValidator.validateLogin(state.email, state.password)?.let { error ->
            _uiState.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = withContext(dispatchers.io) {
                loginUseCase.execute(state.email, state.password)
            }
            result
                .onSuccess { user ->
                    sessionManager.setUser(user)
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    navigateAfterLogin()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = AuthErrorMapper.map(error))
                    }
                }
        }
    }

    fun goToSignup() {
        _events.tryEmit(AuthUiEvent.Navigate(Screen.Signup))
    }

    fun onBackClicked() {
        _events.tryEmit(AuthUiEvent.NavigateBack)
    }

    private fun navigateAfterLogin() {
        val target = NavigationStore.consumeAfterLogin() ?: Screen.Home
        _events.tryEmit(AuthUiEvent.Navigate(target))
    }
}
