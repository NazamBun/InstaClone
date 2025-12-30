package com.nazam.instaclone.feature.home.presentation.viewmodel

/**
 * Events one-shot: navigation + message.
 * Pas dans UiState.
 */
sealed interface HomeUiEvent {
    data object NavigateToLogin : HomeUiEvent
    data object NavigateToSignup : HomeUiEvent
    data object NavigateToCreatePost : HomeUiEvent
    data class ShowMessage(val message: String) : HomeUiEvent
}