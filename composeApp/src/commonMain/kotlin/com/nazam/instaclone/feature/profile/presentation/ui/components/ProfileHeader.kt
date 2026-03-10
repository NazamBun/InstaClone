package com.nazam.instaclone.feature.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUi
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUiTokens
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.dialog_no
import instaclone.composeapp.generated.resources.dialog_yes
import instaclone.composeapp.generated.resources.profile_edit_cover
import instaclone.composeapp.generated.resources.profile_edit_profile
import instaclone.composeapp.generated.resources.profile_follow
import instaclone.composeapp.generated.resources.profile_logout
import instaclone.composeapp.generated.resources.profile_logout_message
import instaclone.composeapp.generated.resources.profile_logout_title
import instaclone.composeapp.generated.resources.profile_message
import instaclone.composeapp.generated.resources.profile_more_cd
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileHeader(
    ui: ProfileUi,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onMoreClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onEditCoverClick: () -> Unit,
    onEditAvatarClick: () -> Unit
) {
    val t = ProfileUiTokens
    var menuOpen by remember { mutableStateOf(false) }
    var logoutConfirm by remember { mutableStateOf(false) }

    if (logoutConfirm) {
        AlertDialog(
            onDismissRequest = { logoutConfirm = false },
            title = { Text(stringResource(Res.string.profile_logout_title)) },
            text = { Text(stringResource(Res.string.profile_logout_message)) },
            confirmButton = {
                Button(onClick = { logoutConfirm = false; onLogoutClick() }) {
                    Text(stringResource(Res.string.dialog_yes))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { logoutConfirm = false }) {
                    Text(stringResource(Res.string.dialog_no))
                }
            }
        )
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
        ) {
            if (!ui.coverUrl.isNullOrBlank()) {
                NetworkImage(
                    url = ui.coverUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(t.SoftOverlay)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(t.CoverGradientStart, t.CoverGradientEnd)
                            )
                        )
                )
            }

            if (ui.isSelfProfile) {
                Surface(
                    color = t.CardBg.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .border(1.dp, t.Border, RoundedCornerShape(999.dp))
                        .clickable(onClick = onEditCoverClick)
                ) {
                    Text(
                        text = stringResource(Res.string.profile_edit_cover),
                        color = t.TextPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(t.CardBg)
                    .border(3.dp, t.Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!ui.avatarUrl.isNullOrBlank()) {
                    NetworkImage(
                        url = ui.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                    )
                }
            }

            if (ui.isSelfProfile) {
                Spacer(Modifier.size(10.dp))
                Surface(
                    color = t.CardBg,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, t.Border, CircleShape)
                        .clickable(onClick = onEditAvatarClick)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = t.TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (ui.isSelfProfile) {
                Button(
                    onClick = onEditProfileClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.profile_edit_profile))
                }

                Box {
                    Surface(
                        color = t.CardBg,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, t.Border, RoundedCornerShape(14.dp))
                            .clickable {
                                onMoreClick()
                                menuOpen = true
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.MoreHoriz,
                                contentDescription = stringResource(Res.string.profile_more_cd),
                                tint = t.TextPrimary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.profile_logout)) },
                            onClick = {
                                menuOpen = false
                                logoutConfirm = true
                            }
                        )
                    }
                }
            } else {
                Button(
                    onClick = onFollowClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.profile_follow))
                }

                OutlinedButton(
                    onClick = onMessageClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.profile_message))
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = ui.displayName,
                color = t.TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = "@${ui.username}", color = t.TextSecondary)

            if (ui.bio.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(text = ui.bio, color = t.TextPrimary)
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}
