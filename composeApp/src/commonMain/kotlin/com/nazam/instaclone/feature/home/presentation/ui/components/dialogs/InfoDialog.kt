package com.nazam.instaclone.feature.home.presentation.ui.components.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun InfoDialog(
    message: String,
    confirmLabel: String?,
    secondaryLabel: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onSecondary: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Information") },
        text = { Text(text = message) },
        confirmButton = {
            if (confirmLabel != null) {
                TextButton(onClick = onConfirm) { Text(confirmLabel) }
            } else {
                TextButton(onClick = onDismiss) { Text("OK") }
            }
        },
        dismissButton = {
            if (secondaryLabel != null) {
                Row {
                    TextButton(onClick = onSecondary) { Text(secondaryLabel) }
                    TextButton(onClick = onDismiss) { Text("Annuler") }
                }
            }
        }
    )
}