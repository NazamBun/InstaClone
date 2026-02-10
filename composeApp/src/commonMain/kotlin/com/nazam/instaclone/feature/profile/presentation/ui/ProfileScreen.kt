package com.nazam.instaclone.feature.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_edit_cover
import instaclone.composeapp.generated.resources.profile_follow
import instaclone.composeapp.generated.resources.profile_followers
import instaclone.composeapp.generated.resources.profile_following
import instaclone.composeapp.generated.resources.profile_joined_prefix
import instaclone.composeapp.generated.resources.profile_message
import instaclone.composeapp.generated.resources.profile_more_cd
import instaclone.composeapp.generated.resources.profile_posts
import instaclone.composeapp.generated.resources.profile_tab_likes
import instaclone.composeapp.generated.resources.profile_tab_media
import instaclone.composeapp.generated.resources.profile_tab_posts
import org.jetbrains.compose.resources.stringResource

/**
 * UI Profil (KMP-friendly)
 * ✅ Pas d'Android-only
 * ✅ Pas de texte en dur (strings.xml)
 * ✅ Prêt pour avatar/cover en URL
 */
@Composable
fun ProfileScreen(
    ui: ProfileUi,
    contentPadding: PaddingValues,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onMoreClick: () -> Unit,
    onEditCoverClick: () -> Unit,
    onEditAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens
    var selectedTab by remember { mutableIntStateOf(0) } // 0=Posts, 1=Media, 2=Likes

    Column(
        modifier = modifier
            .background(t.ScreenBg)
            .padding(contentPadding)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
    ) {
        // --- Cover ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
        ) {
            if (!ui.coverUrl.isNullOrBlank()) {
                NetworkImage(
                    url = ui.coverUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(t.SoftOverlay)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(t.CoverGradientStart, t.CoverGradientEnd)
                            )
                        )
                )
            }

            Surface(
                color = t.CardBg.copy(alpha = 0.75f),
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
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }

        // Avatar + bouton caméra
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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

        Spacer(Modifier.height(12.dp))

        // --- Buttons ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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

            Surface(
                color = t.CardBg,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, t.Border, RoundedCornerShape(14.dp))
                    .clickable(onClick = onMoreClick)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = stringResource(Res.string.profile_more_cd),
                        tint = t.TextPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // --- Infos ---
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = ui.displayName,
                color = t.TextPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "@${ui.username}",
                color = t.TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )

            if (ui.bio.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = ui.bio,
                    color = t.TextPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(12.dp))

            if (ui.location.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = t.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = ui.location,
                        color = t.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(6.dp))
            }

            if (ui.website.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = null,
                        tint = t.Accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = ui.website,
                        color = t.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
            }

            if (ui.joinedLabel.isNotBlank()) {
                Text(
                    text = "${stringResource(Res.string.profile_joined_prefix)} ${ui.joinedLabel}",
                    color = t.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // --- Stats ---
        Surface(
            color = t.CardBg,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .border(1.dp, t.Border, RoundedCornerShape(18.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = ui.postsCount, label = stringResource(Res.string.profile_posts))
                StatItem(value = ui.followersCount, label = stringResource(Res.string.profile_followers))
                StatItem(value = ui.followingCount, label = stringResource(Res.string.profile_following))
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- Tabs ---
        ProfileTabs(
            selectedIndex = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        // --- Grid placeholder ---
        ProfileGridPlaceholder(
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileTabs(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens

    Surface(
        color = t.CardBg,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, t.Border, RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabChip(
                selected = selectedIndex == 0,
                text = stringResource(Res.string.profile_tab_posts),
                onClick = { onSelect(0) },
                modifier = Modifier.weight(1f)
            )
            TabChip(
                selected = selectedIndex == 1,
                text = stringResource(Res.string.profile_tab_media),
                onClick = { onSelect(1) },
                modifier = Modifier.weight(1f)
            )
            TabChip(
                selected = selectedIndex == 2,
                text = stringResource(Res.string.profile_tab_likes),
                onClick = { onSelect(2) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabChip(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens
    val bg = if (selected) t.Accent else t.ScreenBg
    val fg = if (selected) t.ScreenBg else t.TextPrimary

    Surface(
        color = bg,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = fg,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfileGridPlaceholder(
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens

    Column(modifier = modifier.fillMaxWidth()) {
        repeat(2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(t.CardBg)
                            .border(1.dp, t.Border, RoundedCornerShape(16.dp))
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun StatItem(
    value: Int,
    label: String
) {
    val t = ProfileUiTokens

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            color = t.TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = t.TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * UI Model (simple pour l’instant).
 * ✅ Defaults => App.kt ne casse pas
 */
data class ProfileUi(
    val displayName: String,
    val username: String,
    val bio: String,
    val location: String,
    val website: String,
    val joinedLabel: String,
    val postsCount: Int,
    val followersCount: Int,
    val followingCount: Int,
    val avatarUrl: String? = null,
    val coverUrl: String? = null
)
