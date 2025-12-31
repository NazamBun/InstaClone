package com.nazam.instaclone.feature.auth.presentation.viewmodel

/**
 * Events one-shot: seulement navigation.
 * Les erreurs restent dans UiState (ex: ui.errorMessage).
 */
sealed interface AuthUiEvent {
    data object NavigateToHome : AuthUiEvent
    data object NavigateToLogin : AuthUiEvent
    data object NavigateToSignup : AuthUiEvent
    data object NavigateBack : AuthUiEvent
    data object NavigateToCreatePost : AuthUiEvent
}