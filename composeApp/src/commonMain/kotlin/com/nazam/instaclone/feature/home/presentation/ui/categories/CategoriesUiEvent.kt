package com.nazam.instaclone.feature.home.presentation.ui.categories

import com.nazam.instaclone.core.navigation.Screen

sealed interface CategoriesUiEvent {
    data class NavigateBack(val screen: Screen) : CategoriesUiEvent
}