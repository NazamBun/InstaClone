package com.nazam.instaclone.feature.profile.presentation.viewmodel

import com.nazam.instaclone.core.navigation.Screen

/**
 * Events "one-shot" (navigation, snack, etc.).
 * Ici on fait simple : navigation uniquement.
 */
sealed interface ProfileUiEvent {
    data class Navigate(val screen: Screen) : ProfileUiEvent
}