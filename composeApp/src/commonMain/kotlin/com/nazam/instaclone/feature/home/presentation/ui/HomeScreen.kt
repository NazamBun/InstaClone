package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.CommentsPanel
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.CommentInputBar
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.LockedCommentBar
import com.nazam.instaclone.feature.home.presentation.ui.components.dialogs.InfoDialog
import com.nazam.instaclone.feature.home.presentation.ui.components.utils.getUserLetter
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    ui: HomeUiState,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit,

    onCreatePostClick: () -> Unit,
    onLogoutClick: () -> Unit,

    onVoteLeft: (String) -> Unit,
    onVoteRight: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onCloseComments: () -> Unit,

    onNewCommentChange: (String) -> Unit,
    onSendCommentClick: () -> Unit,
    onCommentInputRequested: () -> Unit,

    onConsumeDialog: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { ui.posts.size })
    val density = LocalDensity.current

    var bottomBlockHeightDp by remember { mutableStateOf(0.dp) }
    val panelHeight = 320.dp

    val extraVotesPadding: Dp =
        if (ui.isCommentsSheetOpen) panelHeight + bottomBlockHeightDp else 0.dp

    if (ui.dialogMessage != null) {
        InfoDialog(
            message = ui.dialogMessage ?: "",
            confirmLabel = ui.dialogConfirmLabel,
            secondaryLabel = ui.dialogSecondaryLabel,
            onDismiss = onConsumeDialog,
            onConfirm = {
                val goLogin = ui.dialogShouldOpenLogin
                onConsumeDialog()
                if (goLogin) onNavigateToLogin()
            },
            onSecondary = {
                val goSignup = ui.dialogShouldOpenSignup
                onConsumeDialog()
                if (goSignup) onNavigateToSignup()
            }
        )
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .onSizeChanged { size ->
                        bottomBlockHeightDp = with(density) { size.height.toDp() }
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
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050509))
                .padding(padding)
        ) {

            when {
                ui.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFF4EB8)
                    )
                }

                ui.posts.isEmpty() -> {
                    Text(
                        text = "Aucun post",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { index ->
                        val post = ui.posts[index]
                        val rawOffset =
                            (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
                        val pageOffset = abs(rawOffset)

                        val resultsAlpha = (1f - pageOffset * 1.5f).coerceIn(0f, 1f)
                        val isVoting = ui.votingPostId == post.id

                        VsPostItem(
                            post = post,
                            isVoting = isVoting,
                            onVoteLeft = { onVoteLeft(post.id) },
                            onVoteRight = { onVoteRight(post.id) },
                            resultsAlpha = resultsAlpha,
                            modifier = Modifier.fillMaxSize(),
                            onCommentsClick = { onOpenComments(post.id) },
                            onMessageClick = {},
                            onShareClick = {},
                            extraBottomPadding = extraVotesPadding
                        )
                    }
                }
            }

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