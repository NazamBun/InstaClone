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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_edit_cover
import instaclone.composeapp.generated.resources.profile_edit_avatar_cd
import instaclone.composeapp.generated.resources.profile_follow
import instaclone.composeapp.generated.resources.profile_followers
import instaclone.composeapp.generated.resources.profile_following
import instaclone.composeapp.generated.resources.profile_joined_prefix
import instaclone.composeapp.generated.resources.profile_message
import instaclone.composeapp.generated.resources.profile_more
import instaclone.composeapp.generated.resources.profile_posts
import org.jetbrains.compose.resources.stringResource

/**
 * UI Profil (KMP-friendly)
 * - aucune lib Android-only
 * - pas de texte "en dur" => strings.xml
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
    val screenBg = Color(0xFF050509)
    val cardBg = Color(0xFF0B0B10)
    val accent = Color(0xFFFF4EB8)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFBBBBBB)

    Column(
        modifier = modifier
            .background(screenBg)
            .padding(contentPadding)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
    ) {
        // --- Cover ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1B1B22), Color(0xFF050509))
                    )
                )
        ) {
            Surface(
                color = Color(0xFFFFFFFF).copy(alpha = 0.10f),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clickable(onClick = onEditCoverClick)
            ) {
                Text(
                    text = stringResource(Res.string.profile_edit_cover),
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }

        // Avatar + petit bouton
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
                    .background(Color(0xFF14141A))
                    .border(3.dp, accent, CircleShape)
            )

            Spacer(Modifier.size(10.dp))

            Surface(
                color = cardBg,
                shape = CircleShape,
                modifier = Modifier
                    .size(44.dp)
                    .border(1.dp, Color(0x22FFFFFF), CircleShape)
                    .clickable(onClick = onEditAvatarClick)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "📷",
                        color = textPrimary
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
                color = cardBg,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                    .clickable(onClick = onMoreClick)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(Res.string.profile_more),
                        color = textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // --- Infos ---
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = ui.displayName,
                color = textPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "@${ui.username}",
                color = textSecondary,
                style = MaterialTheme.typography.bodyLarge
            )

            if (ui.bio.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = ui.bio,
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(12.dp))

            // petits détails (location / site / joined)
            if (ui.location.isNotBlank()) {
                Text(
                    text = "📍 ${ui.location}",
                    color = textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (ui.website.isNotBlank()) {
                Text(
                    text = "🔗 ${ui.website}",
                    color = accent,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (ui.joinedLabel.isNotBlank()) {
                Text(
                    text = "🗓️ ${stringResource(Res.string.profile_joined_prefix)} ${ui.joinedLabel}",
                    color = textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // --- Stats ---
        Surface(
            color = cardBg,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(18.dp))
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

        Spacer(Modifier.height(18.dp))

        // Ici plus tard : onglets + grid des posts
    }
}

@Composable
private fun StatItem(
    value: Int,
    label: String
) {
    val textPrimary = Color.White
    val textSecondary = Color(0xFFBBBBBB)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            color = textPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = textSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * UI Model (simple, pour l’instant).
 * Plus tard on mettra un UiState + ViewModel (MVVM) branché à Supabase.
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
    val followingCount: Int
)
