package com.nazam.instaclone.feature.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileGrid
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileHeader
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileStats
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileTabs
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.admin_profile_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen(
    ui: ProfileUi,
    contentPadding: PaddingValues,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onMoreClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onEditCoverClick: () -> Unit,
    onEditAvatarClick: () -> Unit,
    onPostClick: (VsPost) -> Unit,
    isAdmin: Boolean,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTab = remember { mutableStateOf(ProfileTab.POSTS) }

    LazyColumn(
        modifier = modifier.background(ProfileUiTokens.ScreenBg),
        contentPadding = contentPadding
    ) {
        item {
            ProfileHeader(
                ui = ui,
                onFollowClick = onFollowClick,
                onMessageClick = onMessageClick,
                onMoreClick = onMoreClick,
                onLogoutClick = onLogoutClick,
                onEditProfileClick = onEditProfileClick,
                onEditCoverClick = onEditCoverClick,
                onEditAvatarClick = onEditAvatarClick
            )
        }

        item { ProfileStats(ui = ui) }

        if (isAdmin) {
            item {
                Button(
                    onClick = onNavigateToAdmin,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(stringResource(Res.string.admin_profile_button))
                }
            }
        }

        item {
            ProfileTabs(
                selectedTab = selectedTab.value,
                onTabSelected = { selectedTab.value = it }
            )
        }

        item {
            ProfileGrid(
                posts = ui.posts,
                onPostClick = onPostClick
            )
        }
    }
}
