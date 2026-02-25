package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.share.SharePayload
import com.nazam.instaclone.core.ui.UiText

/**
 * Events one-shot : navigation + messages + actions UI (share).
 */
sealed interface HomeUiEvent {

    data class Navigate(val screen: Screen) : HomeUiEvent

    data class ShowMessage(val message: UiText) : HomeUiEvent

    /**
     * L'UI doit déclencher le partage natif.
     */
    data class Share(val payload: SharePayload) : HomeUiEvent
}
