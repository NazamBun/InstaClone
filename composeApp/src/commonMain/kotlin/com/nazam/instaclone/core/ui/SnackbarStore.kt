package com.nazam.instaclone.core.ui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Petit bus de messages (KMP friendly).
 * - Les écrans peuvent "envoyer" un message
 * - App.kt l'écoute et affiche un Snackbar
 */
object SnackbarStore {

    private val _messages = MutableSharedFlow<UiText>(extraBufferCapacity = 1)
    val messages: SharedFlow<UiText> = _messages

    fun show(message: UiText) {
        _messages.tryEmit(message)
    }
}
