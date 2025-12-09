package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {

    val viewModel = remember { HomeViewModel() }
    val ui = viewModel.uiState.collectAsState()

    val pagerState = rememberPagerState(
        pageCount = { ui.value.posts.size }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050509))
    ) {

        when {
            ui.value.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFFF4EB8)
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
                    text = "Aucun post pour l’instant",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp) // hauteur de la bottom bar
                ) { pageIndex ->

                    val post = ui.value.posts[pageIndex]

                    val rawOffset =
                        (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction

                    val pageOffset = kotlin.math.abs(rawOffset)

                    val resultsAlpha = (1f - pageOffset * 1.5f)
                        .coerceIn(0f, 1f)

                    VsPostItem(
                        post = post,
                        onVoteLeft = { viewModel.voteLeft(post.id) },
                        onVoteRight = { viewModel.voteRight(post.id) },
                        resultsAlpha = resultsAlpha,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        HomeBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

