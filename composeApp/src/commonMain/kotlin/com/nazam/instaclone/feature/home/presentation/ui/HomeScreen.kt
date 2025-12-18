package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToCreatePost: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel = remember { HomeViewModel() }
    val ui = viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // 🔔 Snackbar + action "Se connecter"
    LaunchedEffect(ui.value.snackbarMessage) {
        val message = ui.value.snackbarMessage ?: return@LaunchedEffect

        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = ui.value.snackbarActionLabel
        )

        if (result == SnackbarResult.ActionPerformed && ui.value.shouldOpenLogin) {
            onNavigateToLogin()
        }

        viewModel.consumeSnackbar()
    }

    val pagerState = rememberPagerState(
        pageCount = { ui.value.posts.size }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}