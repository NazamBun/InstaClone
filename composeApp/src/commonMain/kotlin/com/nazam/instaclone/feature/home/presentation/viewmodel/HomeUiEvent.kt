package com.nazam.instaclone.feature.home.presentation.viewmodel

/**
 * Événements "one-shot" (1 seule fois) :
 * - navigation
 * - toast/snackbar
 *
 * (Pas stocké dans uiState, car uiState = état durable)
 */
sealed interface HomeUiEvent {
    data object NavigateToLogin : HomeUiEvent
    data object NavigateToSignup : HomeUiEvent
}