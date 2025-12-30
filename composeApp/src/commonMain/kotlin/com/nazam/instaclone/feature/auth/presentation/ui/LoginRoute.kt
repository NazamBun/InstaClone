package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.feature.auth.presentation.viewmodel.AuthUiEvent
import com.nazam.instaclone.feature.auth.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

/**
 * Route = colle l'UI au ViewModel.
 * Elle écoute les events (navigation / messages).
 */
@Composable
fun LoginRoute(
    onNavigateToSignup: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val viewModel: LoginViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AuthUiEvent.NavigateToHome -> onNavigateToHome()
                AuthUiEvent.NavigateToSignup -> onNavigateToSignup()
                AuthUiEvent.NavigateToLogin -> Unit
                AuthUiEvent.NavigateBack -> Unit
                is AuthUiEvent.ShowError -> Unit
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