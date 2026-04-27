package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.presentation.viewmodel.AuthUiEvent
import com.nazam.instaclone.feature.auth.presentation.viewmodel.SignupViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignupRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: SignupViewModel = koinViewModel()
    val ui by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AuthUiEvent.Navigate -> onNavigate(event.screen)
                AuthUiEvent.NavigateBack -> onNavigate(Screen.Login)
            }
        }
    }

    SignupScreen(
        ui = ui,
        onBackClick = viewModel::onBackClicked,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChanged,
        onDisplayNameChange = viewModel::onDisplayNameChanged,
        onSignupClick = viewModel::signup,
        onGoToLoginClick = { onNavigate(Screen.Login) }
    )
}
