package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.share.SharePayload
import com.nazam.instaclone.core.ui.UiText

/**
 * Events one-shot : navigation + message + share.
 */
sealed interface HomeUiEvent {

    data class Navigate(val screen: Screen) : HomeUiEvent

    data class ShowMessage(val message: UiText) : HomeUiEvent

    /**
     * Partage natif (Android/iOS).
     * Le texte est déjà prêt.
     */
    data class Share(val payload: SharePayload) : HomeUiEvent
}
