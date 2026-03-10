package com.nazam.instaclone.feature.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileGrid
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileHeader
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileStats
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileTabs

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
