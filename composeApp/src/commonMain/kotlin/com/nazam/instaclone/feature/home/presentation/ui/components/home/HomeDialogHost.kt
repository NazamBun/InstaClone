package com.nazam.instaclone.feature.home.presentation.ui.components.home

import androidx.compose.runtime.Composable
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.dialogs.InfoDialog

@Composable
fun HomeDialogHost(
    ui: HomeUiState,
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onConsumeDialog: () -> Unit
) {
    val message = ui.dialogMessage ?: return

    InfoDialog(
        message = message,
        confirmLabel = ui.dialogConfirmLabel,
        secondaryLabel = ui.dialogSecondaryLabel,
        onDismiss = onConsumeDialog,
        onConfirm = {
            val goLogin = ui.dialogShouldOpenLogin
            onConsumeDialog()
            if (goLogin) onNavigateToLogin()
        },
        onSecondary = {
            val goSignup = ui.dialogShouldOpenSignup
            onConsumeDialog()
            if (goSignup) onNavigateToSignup()
        }
    )
}