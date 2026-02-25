package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * Events one-shot : navigation + message + share.
 */
sealed interface HomeUiEvent {

    data class Navigate(val screen: Screen) : HomeUiEvent

    data class ShowMessage(val message: UiText) : HomeUiEvent

    /**
     * Share demandé (l'UI génère image + texte et lance le share natif).
     */
    data class Share(val post: VsPost) : HomeUiEvent
}
