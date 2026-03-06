package com.nazam.instaclone.core.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest

/**
 * Écoute SnackbarStore et affiche les messages via SnackbarHostState.
 * Ici, on convertit UiText -> String dans une zone @Composable.
 */
@Composable
fun SnackbarEffect(hostState: SnackbarHostState) {
    var pendingMessage by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(Unit) {
        SnackbarStore.messages.collectLatest { message ->
            pendingMessage = message
        }
    }

    pendingMessage?.let { message ->
        val text = message.asString()

        LaunchedEffect(text) {
            hostState.showSnackbar(text)
            pendingMessage = null
        }
    }
}
