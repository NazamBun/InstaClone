package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * Events one-shot : navigation + messages + actions UI (share).
 */
sealed interface HomeUiEvent {

    data class Navigate(val screen: Screen) : HomeUiEvent

    data class ShowMessage(val message: UiText) : HomeUiEvent

    /**
     * L'UI construit le texte (stringResource) puis lance le share natif.
     */
    data class Share(val post: VsPost) : HomeUiEvent
}
