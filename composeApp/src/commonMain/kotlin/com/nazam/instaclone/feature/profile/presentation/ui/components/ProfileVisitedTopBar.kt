package com.nazam.instaclone.feature.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUi
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUiTokens
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_more_cd
import instaclone.composeapp.generated.resources.profile_visit_back_cd
import instaclone.composeapp.generated.resources.profile_visit_copy_url
import instaclone.composeapp.generated.resources.profile_visit_report
import instaclone.composeapp.generated.resources.profile_visit_restrict
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileVisitedTopBar(
    ui: ProfileUi,
    onBackClick: () -> Unit,
    onCopyUrlClick: () -> Unit,
    onReportClick: () -> Unit,
    onRestrictClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens
    var menuOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(t.ScreenBg)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = stringResource(Res.string.profile_visit_back_cd),
            tint = t.TextPrimary,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(onClick = onBackClick)
                .padding(8.dp)
        )

        Text(
            text = "@${ui.username}",
            color = t.TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Center)
        )

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = stringResource(Res.string.profile_more_cd),
                tint = t.TextPrimary,
                modifier = Modifier
                    .clickable { menuOpen = true }
                    .padding(8.dp)
            )

            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.profile_visit_copy_url)) },
                    onClick = {
                        menuOpen = false
                        onCopyUrlClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.profile_visit_restrict)) },
                    onClick = {
                        menuOpen = false
                        onRestrictClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.profile_visit_report)) },
                    onClick = {
                        menuOpen = false
                        onReportClick()
                    }
                )
            }
        }
    }
}
