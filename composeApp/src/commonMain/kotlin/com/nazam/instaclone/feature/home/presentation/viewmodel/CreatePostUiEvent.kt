package com.nazam.instaclone.feature.home.presentation.viewmodel

/**
 * Events one-shot pour CreatePost.
 * Ici : navigation + message.
 */
sealed interface CreatePostUiEvent {
    data object PostCreated : CreatePostUiEvent
    data object NavigateBack : CreatePostUiEvent
    data object NavigateToLogin : CreatePostUiEvent

    data class ShowMessage(val message: String) : CreatePostUiEvent
}