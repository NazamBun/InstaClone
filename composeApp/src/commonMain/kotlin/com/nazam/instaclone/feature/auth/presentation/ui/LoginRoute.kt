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
        viewModel.checkSession()

        viewModel.events.collectLatest { event ->
            when (event) {
                is AuthUiEvent.Navigate -> onNavigate(event.screen)
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