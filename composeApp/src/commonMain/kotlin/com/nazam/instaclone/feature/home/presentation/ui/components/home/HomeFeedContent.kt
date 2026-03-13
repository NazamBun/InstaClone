package com.nazam.instaclone.feature.home.presentation.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.nazam.instaclone.feature.home.domain.model.FeedMode
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.VsPostItem
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.home_empty
import instaclone.composeapp.generated.resources.home_empty_following
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeFeedContent(
    ui: HomeUiState,
    modifier: Modifier,
    extraBottomPadding: Dp,
    onVoteLeft: (String) -> Unit,
    onVoteRight: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onOpenAuthor: (VsPost) -> Unit,
    onShare: (VsPost) -> Unit,
    onLoadMore: () -> Unit
) {
    Box(modifier = modifier) {
        when {
            ui.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = Color(0xFF2F5BFF))

            ui.posts.isEmpty() -> {
                val text = if (ui.selectedFeedMode == FeedMode.FOLLOWING) {
                    stringResource(Res.string.home_empty_following)
                } else stringResource(Res.string.home_empty)

                Text(text = text, color = Color.White, modifier = Modifier.align(Alignment.Center))
            }

            else -> {
                val pagerState = rememberPagerState(pageCount = { ui.posts.size })

                LaunchedEffect(pagerState.currentPage, ui.posts.size, ui.isLoadingMore, ui.endReached) {
                    val nearEnd = pagerState.currentPage >= (ui.posts.size - 3).coerceAtLeast(0)
                    if (nearEnd && !ui.isLoadingMore && !ui.endReached) onLoadMore()
                }

                VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
                    val post = ui.posts[index]
                    val offset = abs((pagerState.currentPage - index) + pagerState.currentPageOffsetFraction)

                    VsPostItem(
                        post = post,
                        isVoting = ui.votingPostId == post.id,
                        onVoteLeft = { onVoteLeft(post.id) },
                        onVoteRight = { onVoteRight(post.id) },
                        onAuthorClick = onOpenAuthor,
                        resultsAlpha = (1f - offset * 1.5f).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxSize(),
                        onCommentsClick = { onOpenComments(post.id) },
                        onMessageClick = {},
                        onShareClick = { onShare(post) },
                        extraBottomPadding = extraBottomPadding
                    )
                }

                if (ui.isLoadingMore) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        color = Color(0xFF2F5BFF)
                    )
                }
            }
        }
    }
}
