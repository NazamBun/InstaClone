package com.nazam.instaclone.feature.home.presentation.viewmodel

sealed interface CreatePostUiEvent {
    data object PostCreated : CreatePostUiEvent
    data object NavigateBack : CreatePostUiEvent
    data object NavigateToLogin : CreatePostUiEvent
    data object NavigateToCategories : CreatePostUiEvent

    data class ShowMessage(val message: String) : CreatePostUiEvent
}