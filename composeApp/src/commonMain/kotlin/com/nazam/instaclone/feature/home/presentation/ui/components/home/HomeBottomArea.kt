package com.nazam.instaclone.feature.home.presentation.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.HomeBottomBar
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.CommentInputBar
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.LockedCommentBar
import com.nazam.instaclone.feature.home.presentation.ui.components.utils.getUserLetter

@Composable
fun HomeBottomArea(
    ui: HomeUiState,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onLogoutClick: () -> Unit,
    onCreatePostClick: () -> Unit,
    onNewCommentChange: (String) -> Unit,
    onSendCommentClick: () -> Unit,
    onCommentInputRequested: () -> Unit,
    onBottomHeightChanged: (Dp) -> Unit
) {
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .onSizeChanged { size ->
                onBottomHeightChanged(with(density) { size.height.toDp() })
            }
    ) {
        if (ui.isCommentsSheetOpen) {
            if (ui.isLoggedIn) {
                CommentInputBar(
                    letter = getUserLetter(ui.currentUserDisplayName, ui.currentUserEmail),
                    text = ui.newCommentText,
                    onTextChange = onNewCommentChange,
                    onSend = onSendCommentClick
                )
            } else {
                LockedCommentBar(onClick = onCommentInputRequested)
            }
        }

        HomeBottomBar(
            isLoggedIn = ui.isLoggedIn,
            onCreatePostClick = {
                if (ui.isLoggedIn) onNavigateToCreatePost() else onCreatePostClick()
            },
            onLoginClick = onNavigateToLogin,
            onLogoutClick = onLogoutClick
        )
    }
}