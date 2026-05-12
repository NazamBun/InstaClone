package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.ui.UiText

sealed interface CreatePostYesNoUiEvent {
    data object PostCreated : CreatePostYesNoUiEvent
    data object NavigateBack : CreatePostYesNoUiEvent
    data object NavigateToLogin : CreatePostYesNoUiEvent
    data class ShowMessage(val message: UiText) : CreatePostYesNoUiEvent
}
