package com.nazam.instaclone.feature.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileGrid
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileHeader
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileStats
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileTabs

/**
 * ProfileScreen (orchestrateur)
 * - 1 seul rôle : assembler les blocs UI
 * - KMP friendly
 */
@Composable
fun ProfileScreen(
    ui: ProfileUi,
    contentPadding: PaddingValues,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onMoreClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditCoverClick: () -> Unit,
    onEditAvatarClick: () -> Unit,
    onPostClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens
    var selectedTab by remember { mutableStateOf(ProfileTab.POSTS) }

    LazyColumn(
        modifier = modifier.background(t.ScreenBg),
        contentPadding = contentPadding
    ) {
        item {
            ProfileHeader(
                ui = ui,
                onFollowClick = onFollowClick,
                onMessageClick = onMessageClick,
                onMoreClick = onMoreClick,
                onLogoutClick = onLogoutClick,
                onEditCoverClick = onEditCoverClick,
                onEditAvatarClick = onEditAvatarClick
            )
        }

        item { ProfileStats(ui = ui) }

        item {
            ProfileTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        item {
            // Pour l'instant : même liste pour tous les tabs (on branchera plus tard)
            ProfileGrid(
                posts = ui.posts,
                onPostClick = onPostClick
            )
        }
    }
}
