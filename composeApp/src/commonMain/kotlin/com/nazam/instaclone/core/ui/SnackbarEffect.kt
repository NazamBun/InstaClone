package com.nazam.instaclone.core.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest

/**
 * Écoute SnackbarStore et affiche les messages via SnackbarHostState.
 */
@Composable
fun SnackbarEffect(hostState: SnackbarHostState) {
    LaunchedEffect(Unit) {
        SnackbarStore.messages.collectLatest { message ->
            hostState.showSnackbar(message.asString())
        }
    }
}
