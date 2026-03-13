package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.ui.UiText

sealed interface CreatePostUiEvent {
    data object PostCreated : CreatePostUiEvent
    data object NavigateBack : CreatePostUiEvent
    data object NavigateToLogin : CreatePostUiEvent
    data class ShowMessage(val message: UiText) : CreatePostUiEvent
}
