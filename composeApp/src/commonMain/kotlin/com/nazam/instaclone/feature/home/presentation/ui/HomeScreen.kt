package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.CommentsPanel
import com.nazam.instaclone.feature.home.presentation.ui.components.dialogs.InfoDialog
import com.nazam.instaclone.feature.home.presentation.ui.components.home.HomeBottomArea
import com.nazam.instaclone.feature.home.presentation.ui.components.home.HomeFeedContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    ui: HomeUiState,
    snackbarHostState: SnackbarHostState,

    onCreatePostClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,

    onVoteLeft: (String) -> Unit,
    onVoteRight: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onCloseComments: () -> Unit,

    onNewCommentChange: (String) -> Unit,
    onSendCommentClick: () -> Unit,
    onCommentInputRequested: () -> Unit,

    onConsumeDialog: () -> Unit,
    onDialogConfirm: () -> Unit,
    onDialogSecondary: () -> Unit
) {
    var bottomBlockHeightDp by remember { mutableStateOf(0.dp) }
    val panelHeight = 320.dp

    val extraBottomPadding: Dp =
        if (ui.isCommentsSheetOpen) panelHeight + bottomBlockHeightDp else 0.dp

    if (ui.dialogMessage != null) {
        InfoDialog(
            message = ui.dialogMessage ?: "",
            confirmLabel = ui.dialogConfirmLabel,
            secondaryLabel = ui.dialogSecondaryLabel,
            onDismiss = onConsumeDialog,
            onConfirm = onDialogConfirm,
            onSecondary = onDialogSecondary
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            HomeBottomArea(
                ui = ui,
                onCreatePostClick = onCreatePostClick,
                onLoginClick = onLoginClick,
                onLogoutClick = onLogoutClick,
                onNewCommentChange = onNewCommentChange,
                onSendCommentClick = onSendCommentClick,
                onCommentInputRequested = onCommentInputRequested,
                onBottomHeightChanged = { newHeight -> bottomBlockHeightDp = newHeight }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050509))
                .padding(padding)
        ) {
            HomeFeedContent(
                ui = ui,
                extraBottomPadding = extraBottomPadding,
                onVoteLeft = onVoteLeft,
                onVoteRight = onVoteRight,
                onOpenComments = onOpenComments
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
}