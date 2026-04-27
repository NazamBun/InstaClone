package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.presentation.viewmodel.AuthUiEvent
import com.nazam.instaclone.feature.auth.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: LoginViewModel = koinViewModel()
    val ui by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkSession()
        viewModel.events.collectLatest { event ->
            when (event) {
                is AuthUiEvent.Navigate -> onNavigate(event.screen)
                AuthUiEvent.NavigateBack -> {
                    val back = NavigationStore.consumeAuthReturn() ?: Screen.Home
                    onNavigate(back)
                }
            }
        }
    }

    LoginScreen(
        ui = ui,
        onBackClick = viewModel::onBackClicked,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onLoginClick = viewModel::login,
        onSignupClick = viewModel::goToSignup
    )
}
