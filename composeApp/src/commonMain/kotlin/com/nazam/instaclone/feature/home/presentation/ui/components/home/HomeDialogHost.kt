package com.nazam.instaclone.feature.home.presentation.ui.components.home

import androidx.compose.runtime.Composable
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.dialogs.InfoDialog

/**
 * Gère l’affichage du dialog Home (auth required, etc.)
 * ✅ Conversion UiText -> String UNIQUEMENT ici (côté UI)
 */
@Composable
fun HomeDialogHost(
    ui: HomeUiState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onSecondary: () -> Unit
) {
    val message = ui.dialogMessage ?: return

    InfoDialog(
        message = message.asString(),
        confirmLabel = ui.dialogConfirmLabel?.asString(),
        secondaryLabel = ui.dialogSecondaryLabel?.asString(),
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        onSecondary = onSecondary
    )
}