package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreatePost: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel = remember { HomeViewModel() }
    val ui = viewModel.uiState.collectAsState()

    val pagerState = rememberPagerState(
        pageCount = { ui.value.posts.size }
    )

    // ✅ POPUP PRO (Dialog)
    if (ui.value.dialogMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.consumeDialog() },
            title = { Text(text = "Information") },
            text = { Text(text = ui.value.dialogMessage ?: "") },
            confirmButton = {
                val confirmLabel = ui.value.dialogConfirmLabel
                if (confirmLabel != null) {
                    TextButton(
                        onClick = {
                            val goLogin = ui.value.dialogShouldOpenLogin
                            viewModel.consumeDialog()
                            if (goLogin) onNavigateToLogin()
                        }
                    ) {
                        Text(confirmLabel)
                    }
                } else {
                    TextButton(onClick = { viewModel.consumeDialog() }) {
                        Text("OK")
                    }
                }
            },
            dismissButton = {
                if (ui.value.dialogConfirmLabel != null) {
                    TextButton(onClick = { viewModel.consumeDialog() }) {
                        Text("Annuler")
                    }
                }
            }
        )
    }

    // ✅ BottomSheet commentaires
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (ui.value.isCommentsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeComments() },
            sheetState = sheetState
        ) {
            // Pour l’instant : contenu simple, juste pour tester
            val postId = ui.value.commentsPostId ?: ""

            Text(
                text = "Commentaires du post: $postId",
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Ici on affichera la liste des commentaires plus tard ✅",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    androidx.compose.material3.Scaffold(
        bottomBar = {
            HomeBottomBar(
                isLoggedIn = ui.value.isLoggedIn,
                onCreatePostClick = {
                    if (ui.value.isLoggedIn) {
                        onNavigateToCreatePost()
                    } else {
                        viewModel.onCreatePostClicked()
                    }
                },
                onLoginClick = onNavigateToLogin,
                onLogoutClick = { viewModel.logout() }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050509))
                .padding(padding)
        ) {
            when {
                ui.value.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFF4EB8)
                    )
                }

                ui.value.posts.isEmpty() -> {
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

                        val post = ui.value.posts[index]

                        val rawOffset =
                            (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
                        val pageOffset = abs(rawOffset)

                        val resultsAlpha = (1f - pageOffset * 1.5f).coerceIn(0f, 1f)
                        val isVoting = ui.value.votingPostId == post.id

                        VsPostItem(
                            post = post,
                            isVoting = isVoting,
                            onVoteLeft = { viewModel.voteLeft(post.id) },
                            onVoteRight = { viewModel.voteRight(post.id) },
                            resultsAlpha = resultsAlpha,
                            modifier = Modifier.fillMaxSize(),

                            // ✅ ICI on branche les icônes
                            onCommentsClick = { viewModel.openComments(post.id) },
                            onMessageClick = { /* plus tard */ },
                            onShareClick = { /* plus tard */ }
                        )
                    }
                }
            }
        }
    }
}