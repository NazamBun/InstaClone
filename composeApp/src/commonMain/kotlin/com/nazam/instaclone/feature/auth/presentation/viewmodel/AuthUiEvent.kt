package com.nazam.instaclone.feature.auth.presentation.viewmodel

/**
 * Evénements UI one-shot.
 * Ici : uniquement la navigation.
 * (Les erreurs restent dans UiState.errorMessage)
 */
sealed interface AuthUiEvent {
    data object NavigateToHome : AuthUiEvent
    data object NavigateToLogin : AuthUiEvent
    data object NavigateToSignup : AuthUiEvent
    data object NavigateBack : AuthUiEvent
}