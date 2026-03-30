package com.nazam.instaclone.feature.home.presentation.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.CommentInputBar
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.LockedCommentBar
import com.nazam.instaclone.feature.home.presentation.ui.components.utils.getUserLetter

@Composable
fun HomeBottomArea(
    ui: HomeUiState,
    onNewCommentChange: (String) -> Unit,
    onSendCommentClick: () -> Unit,
    onCommentInputRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF050509))
            .imePadding()
    ) {
        if (ui.isCommentsSheetOpen) {
            if (ui.isLoggedIn) {
                CommentInputBar(
                    letter = getUserLetter(ui.currentUserDisplayName, ui.currentUserEmail),
                    text = ui.newCommentText,
                    isSending = ui.isSendingComment,
                    onTextChange = onNewCommentChange,
                    onSend = onSendCommentClick
                )
            } else {
                LockedCommentBar(onClick = onCommentInputRequested)
            }
        }
    }
}
