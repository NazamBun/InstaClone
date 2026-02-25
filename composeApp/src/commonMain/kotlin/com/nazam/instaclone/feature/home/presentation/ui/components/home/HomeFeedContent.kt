package com.nazam.instaclone.feature.home.presentation.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.VsPostItem
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.home_empty
import instaclone.composeapp.generated.resources.home_empty_filtered
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeFeedContent(
    ui: HomeUiState,
    extraBottomPadding: Dp,
    onVoteLeft: (String) -> Unit,
    onVoteRight: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onShare: (VsPost) -> Unit
) {
    val visiblePosts =
        if (ui.selectedCategoryId.isBlank()) ui.posts
        else ui.posts.filter { it.category == ui.selectedCategoryId }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            ui.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFFF4EB8)
                )
            }

            visiblePosts.isEmpty() -> {
                val labelRes =
                    if (ui.selectedCategoryId.isBlank()) Res.string.home_empty
                    else Res.string.home_empty_filtered

                Text(
                    text = stringResource(labelRes),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                val pagerState = rememberPagerState(pageCount = { visiblePosts.size })

                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { index ->
                    val post = visiblePosts[index]

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
                        onShareClick = { onShare(post) },
                        extraBottomPadding = extraBottomPadding
                    )
                }
            }
        }
    }
}
