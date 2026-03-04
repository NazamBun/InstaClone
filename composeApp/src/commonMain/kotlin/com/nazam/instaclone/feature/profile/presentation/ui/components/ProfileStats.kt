package com.nazam.instaclone.feature.profile.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUi
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUiTokens
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_followers
import instaclone.composeapp.generated.resources.profile_following
import instaclone.composeapp.generated.resources.profile_posts
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileStats(ui: ProfileUi) {
    val t = ProfileUiTokens

    Surface(
        color = t.CardBg,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(1.dp, t.Border, RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = ui.postsCount, label = stringResource(Res.string.profile_posts))
            StatItem(value = ui.followersCount, label = stringResource(Res.string.profile_followers))
            StatItem(value = ui.followingCount, label = stringResource(Res.string.profile_following))
        }
    }

    Spacer(Modifier.height(16.dp))
}

@Composable
private fun StatItem(value: Int, label: String) {
    val t = ProfileUiTokens
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            color = t.TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(text = label, color = t.TextSecondary)
    }
}
