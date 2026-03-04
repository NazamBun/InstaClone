package com.nazam.instaclone.feature.profile.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileTab
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUiTokens
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_tab_likes
import instaclone.composeapp.generated.resources.profile_tab_media
import instaclone.composeapp.generated.resources.profile_tab_posts
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileTabs(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    val t = ProfileUiTokens

    Surface(
        color = t.CardBg,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabChip(
                selected = selectedTab == ProfileTab.POSTS,
                text = stringResource(Res.string.profile_tab_posts),
                onClick = { onTabSelected(ProfileTab.POSTS) },
                modifier = Modifier.weight(1f)
            )
            TabChip(
                selected = selectedTab == ProfileTab.MEDIA,
                text = stringResource(Res.string.profile_tab_media),
                onClick = { onTabSelected(ProfileTab.MEDIA) },
                modifier = Modifier.weight(1f)
            )
            TabChip(
                selected = selectedTab == ProfileTab.LIKES,
                text = stringResource(Res.string.profile_tab_likes),
                onClick = { onTabSelected(ProfileTab.LIKES) },
                modifier = Modifier.weight(1f)
            )
        }
    }

    Spacer(Modifier.height(12.dp))
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
        modifier = modifier.height(40.dp).clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = fg, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}
