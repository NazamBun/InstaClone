package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen() {
    val viewModel = remember { HomeViewModel() }
    val ui = viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        when {
            ui.value.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }

            ui.value.errorMessage != null -> {
                Text(
                    text = ui.value.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
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
                val pagerState = rememberPagerState(
                    pageCount = { ui.value.posts.size }
                )

                // ✅ UN POST = UN ÉCRAN
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val post = ui.value.posts[page]

                    VsPostItem(
                        post = post,
                        onVoteLeft = { viewModel.voteLeft(post.id) },
                        onVoteRight = { viewModel.voteRight(post.id) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}