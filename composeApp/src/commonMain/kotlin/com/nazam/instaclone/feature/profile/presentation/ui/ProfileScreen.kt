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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.dialog_no
import instaclone.composeapp.generated.resources.dialog_yes
import instaclone.composeapp.generated.resources.profile_edit_cover
import instaclone.composeapp.generated.resources.profile_follow
import instaclone.composeapp.generated.resources.profile_followers
import instaclone.composeapp.generated.resources.profile_following
import instaclone.composeapp.generated.resources.profile_joined_prefix
import instaclone.composeapp.generated.resources.profile_logout
import instaclone.composeapp.generated.resources.profile_logout_message
import instaclone.composeapp.generated.resources.profile_logout_title
import instaclone.composeapp.generated.resources.profile_message
import instaclone.composeapp.generated.resources.profile_more_cd
import instaclone.composeapp.generated.resources.profile_posts
import instaclone.composeapp.generated.resources.profile_tab_likes
import instaclone.composeapp.generated.resources.profile_tab_media
import instaclone.composeapp.generated.resources.profile_tab_posts
import instaclone.composeapp.generated.resources.vspost_votes_count
import instaclone.composeapp.generated.resources.vs_title_format
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max

/**
 * UI Profil (KMP-friendly)
 * ✅ Pas d'Android-only
 * ✅ Strings via composeResources
 * ✅ Grille de VsPost
 */
@Composable
fun ProfileScreen(
    ui: ProfileUi,
    contentPadding: PaddingValues,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onMoreClick: () -> Unit,     // compat
    onLogoutClick: () -> Unit,   // action logout
    onEditCoverClick: () -> Unit,
    onEditAvatarClick: () -> Unit,
    onPostClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens
    var selectedTab by remember { mutableIntStateOf(0) }

    var isMenuOpen by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text(stringResource(Res.string.profile_logout_title)) },
            text = { Text(stringResource(Res.string.profile_logout_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogoutClick()
                    }
                ) { Text(stringResource(Res.string.dialog_yes)) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutConfirm = false }) {
                    Text(stringResource(Res.string.dialog_no))
                }
            }
        )
    }

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

        // Avatar + caméra
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

            // "..." + menu
            Box {
                Surface(
                    color = t.CardBg,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .size(48.dp)
                        .border(1.dp, t.Border, RoundedCornerShape(14.dp))
                        .clickable {
                            onMoreClick() // compat (si tu veux une action plus tard)
                            isMenuOpen = true
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
                    expanded = isMenuOpen,
                    onDismissRequest = { isMenuOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.profile_logout)) },
                        onClick = {
                            isMenuOpen = false
                            showLogoutConfirm = true
                        }
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

        // --- Grid ---
        ProfileVsGrid(
            posts = ui.posts,
            onPostClick = onPostClick,
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
private fun ProfileVsGrid(
    posts: List<VsPost>,
    onPostClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(
            items = posts,
            key = { it.id }
        ) { post ->
            VsGridItem(
                post = post,
                onClick = { onPostClick(post) }
            )
        }

        if (posts.isEmpty()) {
            items(6) {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(t.CardBg)
                        .border(1.dp, t.Border, RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Composable
private fun VsGridItem(
    post: VsPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens

    val totalVotes = max(post.totalVotesCount, 1)
    val winnerUrl =
        if (post.leftVotesCount >= post.rightVotesCount) post.leftImageUrl else post.rightImageUrl

    val title = stringResource(Res.string.vs_title_format, post.leftLabel, post.rightLabel)
    val votes = stringResource(Res.string.vspost_votes_count, totalVotes)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        NetworkImage(
            url = winnerUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(t.SoftOverlay)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = title,
                color = t.TextPrimary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = votes,
                color = t.TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
    val coverUrl: String? = null,
    val posts: List<VsPost> = emptyList()
)