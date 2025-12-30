package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.feature.auth.presentation.viewmodel.AuthUiEvent
import com.nazam.instaclone.feature.auth.presentation.viewmodel.SignupViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

/**
 * Route = colle l'UI au ViewModel.
 * Elle écoute les events (navigation / messages).
 */
@Composable
fun SignupRoute(
    onNavigateToLogin: () -> Unit
) {
    val viewModel: SignupViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AuthUiEvent.NavigateBack -> onNavigateToLogin()
                AuthUiEvent.NavigateToLogin -> onNavigateToLogin()
                AuthUiEvent.NavigateToHome -> Unit
                AuthUiEvent.NavigateToSignup -> Unit
                is AuthUiEvent.ShowError -> Unit
            }
        }
    }

    SignupScreen(
        ui = ui,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onDisplayNameChange = viewModel::onDisplayNameChanged,
        onSignupClick = viewModel::signup,
        onGoToLoginClick = onNavigateToLogin
    )
}