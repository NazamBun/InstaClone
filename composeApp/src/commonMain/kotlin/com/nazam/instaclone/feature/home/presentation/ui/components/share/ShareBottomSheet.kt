package com.nazam.instaclone.feature.home.presentation.ui.components.share

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    previewTitle: String,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onCopyLink: () -> Unit,
    onCopyText: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(stringResource(Res.string.share_sheet_title))
            Spacer(Modifier.height(6.dp))
            Text(previewTitle.take(80))
            Spacer(Modifier.height(14.dp))

            TextButton(onClick = onShare, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(Res.string.share_sheet_share))
            }

            TextButton(onClick = onCopyLink, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(Res.string.share_sheet_copy_link))
            }

            TextButton(onClick = onCopyText, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(Res.string.share_sheet_copy_text))
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}
