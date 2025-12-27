package com.nazam.instaclone.feature.home.presentation.viewmodel

import kotlinx.coroutines.flow.update

internal fun HomeViewModel.handleAuthOrGenericError(error: Throwable) {
    if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
        showAuthRequiredDialog("Tu dois être connecté ou créer un compte.")
    } else {
        showInfoDialog(error.message ?: "Une erreur est arrivée")
    }
}

internal fun HomeViewModel.showAuthRequiredDialog(message: String) {
    _uiState.update {
        it.copy(
            dialogMessage = message,
            dialogConfirmLabel = "Se connecter",
            dialogSecondaryLabel = "Créer un compte",
            dialogShouldOpenLogin = true,
            dialogShouldOpenSignup = true
        )
    }
}

internal fun HomeViewModel.showInfoDialog(message: String) {
    _uiState.update {
        it.copy(
            dialogMessage = message,
            dialogConfirmLabel = null,
            dialogSecondaryLabel = null,
            dialogShouldOpenLogin = false,
            dialogShouldOpenSignup = false
        )
    }
}