package com.nazam.instaclone.feature.auth.presentation.viewmodel

import com.nazam.instaclone.core.navigation.Screen

/**
 * Events one-shot: navigation uniquement.
 * On envoie directement la destination.
 */
sealed interface AuthUiEvent {

    data class Navigate(val screen: Screen) : AuthUiEvent

    data object NavigateBack : AuthUiEvent
}