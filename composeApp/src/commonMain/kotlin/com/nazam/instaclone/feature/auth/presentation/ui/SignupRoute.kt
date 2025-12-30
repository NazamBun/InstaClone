package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.presentation.viewmodel.AuthUiEvent
import com.nazam.instaclone.feature.auth.presentation.viewmodel.SignupViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun SignupRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: SignupViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AuthUiEvent.NavigateBack -> onNavigate(Screen.Login)
                AuthUiEvent.NavigateToLogin -> onNavigate(Screen.Login)
                AuthUiEvent.NavigateToHome -> onNavigate(Screen.Home)
                AuthUiEvent.NavigateToSignup -> onNavigate(Screen.Signup)
            }
        }
    }

    SignupScreen(
        ui = ui,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onDisplayNameChange = viewModel::onDisplayNameChanged,
        onSignupClick = viewModel::signup,
        onGoToLoginClick = { onNavigate(Screen.Login) }
    )
}