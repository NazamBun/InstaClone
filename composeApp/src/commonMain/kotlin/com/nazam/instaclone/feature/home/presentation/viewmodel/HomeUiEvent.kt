package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText

/**
 * Events one-shot : navigation + message.
 * Pas dans UiState.
 */
sealed interface HomeUiEvent {

    data class Navigate(val screen: Screen) : HomeUiEvent

    /**
     * ✅ UiText = KMP + traduisible
     */
    data class ShowMessage(val message: UiText) : HomeUiEvent
}