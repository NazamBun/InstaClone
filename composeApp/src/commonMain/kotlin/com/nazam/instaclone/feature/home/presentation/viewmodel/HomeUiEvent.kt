package com.nazam.instaclone.feature.home.presentation.viewmodel

/**
 * Events "one-shot" (une seule fois).
 * On met ici: navigation et messages.
 * On ne met pas ça dans UiState (UiState = état durable).
 */
sealed interface HomeUiEvent {
    data object NavigateToLogin : HomeUiEvent
    data object NavigateToSignup : HomeUiEvent
    data object NavigateToCreatePost : HomeUiEvent

    data class ShowMessage(val message: String) : HomeUiEvent
}