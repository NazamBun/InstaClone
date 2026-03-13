package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.VsPostItem
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.explore_back
import instaclone.composeapp.generated.resources.explore_empty_hashtag
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExplorePagerScreen(
    ui: HomeUiState,
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onVoteLeft: (String) -> Unit,
    onVoteRight: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onOpenAuthor: (VsPost) -> Unit
) {
    val savedPostIds = ExplorePagerStore.getPostIds()
    val startPostId = ExplorePagerStore.getStartPostId()
    val posts = ui.posts.filter { it.id in savedPostIds }

    if (posts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF050509)).padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(Res.string.explore_empty_hashtag), color = Color.White)
        }
        return
    }

    val startIndex = posts.indexOfFirst { it.id == startPostId }.let { if (it >= 0) it else 0 }
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { posts.size })

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF050509)).padding(contentPadding)
    ) {
        Text(
            text = stringResource(Res.string.explore_back),
            color = Color.White,
            modifier = Modifier.align(Alignment.TopStart).padding(horizontal = 16.dp, vertical = 12.dp).clickable(onClick = onBackClick)
        )

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
            val post = posts[index]

            VsPostItem(
                post = post,
                isVoting = ui.votingPostId == post.id,
                onVoteLeft = { onVoteLeft(post.id) },
                onVoteRight = { onVoteRight(post.id) },
                onAuthorClick = onOpenAuthor,
                resultsAlpha = 1f,
                modifier = Modifier.fillMaxSize(),
                onCommentsClick = { onOpenComments(post.id) },
                onMessageClick = {},
                onShareClick = {},
                extraBottomPadding = 0.dp
            )
        }
    }
}
