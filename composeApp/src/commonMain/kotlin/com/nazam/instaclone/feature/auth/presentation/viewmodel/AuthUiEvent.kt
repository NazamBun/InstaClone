package com.nazam.instaclone.feature.auth.presentation.viewmodel

/**
 * ✅ Events UI "one-shot"
 * - Navigation
 * - Message
 * - Action unique
 *
 * ❌ Pas d’état ici
 */
sealed interface AuthUiEvent {

    // Navigation
    object NavigateToHome : AuthUiEvent
    object NavigateToLogin : AuthUiEvent
    object NavigateToSignup : AuthUiEvent
    object NavigateBack : AuthUiEvent

    // Message
    data class ShowError(val message: String) : AuthUiEvent
}