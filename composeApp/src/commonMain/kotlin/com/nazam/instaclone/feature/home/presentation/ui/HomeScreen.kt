package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.CommentsPanel
import com.nazam.instaclone.feature.home.presentation.ui.components.dialogs.InfoDialog
import com.nazam.instaclone.feature.home.presentation.ui.components.home.HomeBottomArea
import com.nazam.instaclone.feature.home.presentation.ui.components.home.HomeFeedContent

@Composable
fun HomeScreen(
    ui: HomeUiState,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onCreatePostClick: () -> Unit,
    onVoteLeft: (String) -> Unit,
    onVoteRight: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onCloseComments: () -> Unit,
    onOpenAuthor: (VsPost) -> Unit,
    onShare: (VsPost) -> Unit,
    onNewCommentChange: (String) -> Unit,
    onSendCommentClick: () -> Unit,
    onCommentInputRequested: () -> Unit,
    onConsumeDialog: () -> Unit,
    onDialogConfirm: () -> Unit,
    onDialogSecondary: () -> Unit,
    onLoadMore: () -> Unit
) {
    val density = LocalDensity.current
    var bottomBlockHeightDp by remember { mutableStateOf(0.dp) }
    val panelHeight = 320.dp

    val extraBottomPadding: Dp =
        if (ui.isCommentsSheetOpen) panelHeight + bottomBlockHeightDp else 0.dp

    ui.dialogMessage?.let { messageUiText ->
        InfoDialog(
            message = messageUiText.asString(),
            confirmLabel = ui.dialogConfirmLabel?.asString(),
            secondaryLabel = ui.dialogSecondaryLabel?.asString(),
            onDismiss = onConsumeDialog,
            onConfirm = onDialogConfirm,
            onSecondary = onDialogSecondary
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050509))
            .padding(contentPadding)
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        HomeFeedContent(
            ui = ui,
            extraBottomPadding = extraBottomPadding,
            onVoteLeft = onVoteLeft,
            onVoteRight = onVoteRight,
            onOpenComments = onOpenComments,
            onOpenAuthor = onOpenAuthor,
            onShare = onShare,
            onLoadMore = onLoadMore
        )

        HomeBottomArea(
            ui = ui,
            onNewCommentChange = onNewCommentChange,
            onSendCommentClick = onSendCommentClick,
            onCommentInputRequested = onCommentInputRequested,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .onSizeChanged { size ->
                    bottomBlockHeightDp = with(density) { size.height.toDp() }
                }
        )

        if (ui.isCommentsSheetOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000))
                    .clickable { onCloseComments() }
            )

            CommentsPanel(
                bottomOffset = bottomBlockHeightDp,
                height = panelHeight,
                isLoading = ui.isCommentsLoading,
                comments = ui.comments,
                onClose = onCloseComments
            )
        }
    }
}
