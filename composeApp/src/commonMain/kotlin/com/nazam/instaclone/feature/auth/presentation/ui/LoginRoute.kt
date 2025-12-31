package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.presentation.viewmodel.AuthUiEvent
import com.nazam.instaclone.feature.auth.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun LoginRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: LoginViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    LaunchedEffect(Unit) {
        // Important : si déjà connecté, on redirige tout de suite
        viewModel.checkSession()

        viewModel.events.collectLatest { event ->
            when (event) {
                AuthUiEvent.NavigateToHome -> onNavigate(Screen.Home)
                AuthUiEvent.NavigateToSignup -> onNavigate(Screen.Signup)
                AuthUiEvent.NavigateToLogin -> onNavigate(Screen.Login)
                AuthUiEvent.NavigateToCreatePost -> onNavigate(Screen.CreatePost)
                AuthUiEvent.NavigateBack -> Unit
            }
        }
    }

    LoginScreen(
        ui = ui,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onLoginClick = viewModel::login,
        onSignupClick = viewModel::goToSignup
    )
}