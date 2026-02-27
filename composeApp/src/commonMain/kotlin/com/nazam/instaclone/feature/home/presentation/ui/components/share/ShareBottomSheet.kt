package com.nazam.instaclone.feature.home.presentation.ui.components.share

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.share_sheet_copy_link
import instaclone.composeapp.generated.resources.share_sheet_copy_text
import instaclone.composeapp.generated.resources.share_sheet_share
import instaclone.composeapp.generated.resources.share_sheet_title
import org.jetbrains.compose.resources.stringResource

/**
 * Bottom sheet "pro" pour le partage.
 * ✅ KMP friendly
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    previewTitle: String,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onCopyLink: () -> Unit,
    onCopyText: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = stringResource(Res.string.share_sheet_title),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = previewTitle,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.share_sheet_share))
                }

                OutlinedButton(
                    onClick = onCopyLink,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.share_sheet_copy_link))
                }
            }

            Spacer(Modifier.height(6.dp))

            TextButton(
                onClick = onCopyText,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.share_sheet_copy_text))
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}
