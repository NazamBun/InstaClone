package com.nazam.instaclone.feature.home.presentation.viewmodel

/**
 * Events one-shot pour CreatePost.
 */
sealed interface CreatePostUiEvent {
    data object PostCreated : CreatePostUiEvent
    data object NavigateBack : CreatePostUiEvent
    data class ShowMessage(val message: String) : CreatePostUiEvent
}