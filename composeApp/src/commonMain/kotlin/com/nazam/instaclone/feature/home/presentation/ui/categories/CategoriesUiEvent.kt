package com.nazam.instaclone.feature.home.presentation.ui.categories

sealed interface CategoriesUiEvent {
    data object NavigateBackToCreatePost : CategoriesUiEvent
}