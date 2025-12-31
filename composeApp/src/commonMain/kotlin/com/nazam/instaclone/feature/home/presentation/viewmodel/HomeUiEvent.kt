package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.navigation.Screen

/**
 * Events one-shot : navigation + message.
 * Pas dans UiState.
 */
sealed interface HomeUiEvent {

    /**
     * Navigation simple : on envoie directement l'écran cible.
     * (Comme AuthUiEvent.Navigate)
     */
    data class Navigate(val screen: Screen) : HomeUiEvent

    /**
     * Message one-shot (snackbar)
     */
    data class ShowMessage(val message: String) : HomeUiEvent
}