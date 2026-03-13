package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.ExploreUiState
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExplorePostTile
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreSearchBar
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreSortSelector
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreUiTokens
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.action_retry
import instaclone.composeapp.generated.resources.explore_empty_hashtag
import instaclone.composeapp.generated.resources.explore_loading
import instaclone.composeapp.generated.resources.explore_sort_controversial_title
import instaclone.composeapp.generated.resources.explore_sort_hot_title
import instaclone.composeapp.generated.resources.explore_sort_recent_title
import instaclone.composeapp.generated.resources.explore_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExploreScreen(
    exploreUi: ExploreUiState,
    contentPadding: PaddingValues,
    onSortSelected: (ExploreSortMode) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onPostClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()
    val visiblePosts = filterPostsByHashtag(
        posts = sortExplorePosts(exploreUi.posts, exploreUi.sortMode),
        query = exploreUi.searchQuery
    )

    ExploreLoadMoreEffect(
        gridState = gridState,
        isLoading = exploreUi.isLoading,
        isLoadingMore = exploreUi.isLoadingMore,
        endReached = exploreUi.endReached,
        onLoadMore = onLoadMore
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ExploreUiTokens.ScreenBackground)
            .padding(contentPadding)
    ) {
        when {
            exploreUi.isLoading -> {
                ExploreCenteredState(stringResource(Res.string.explore_loading)) {
                    CircularProgressIndicator()
                }
            }

            exploreUi.error != null && exploreUi.posts.isEmpty() -> {
                ExploreCenteredState(exploreUi.error.asString()) {
                    Button(onClick = onRetry) {
                        Text(stringResource(Res.string.action_retry))
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.explore_title),
                        color = ExploreUiTokens.TitleColor,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(12.dp))

                    ExploreSearchBar(
                        query = exploreUi.searchQuery,
                        suggestions = listOf("cr7", "mbappe", "barca", "real"),
                        onQueryChange = onSearchQueryChanged,
                        onSuggestionClick = onSearchQueryChanged
                    )

                    Spacer(Modifier.height(12.dp))

                    ExploreSortSelector(
                        selected = exploreUi.sortMode,
                        onSelected = onSortSelected
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = buildExploreTitle(exploreUi.sortMode),
                        color = ExploreUiTokens.SubtitleColor,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(10.dp))

                    if (visiblePosts.isEmpty()) {
                        ExploreEmptyState()
                    } else {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(3),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(visiblePosts) { post ->
                                ExplorePostTile(post = post, onClick = onPostClick)
                            }

                            if (exploreUi.isLoadingMore) {
                                item(span = { GridItemSpan(3) }) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreEmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(Res.string.explore_empty_hashtag),
            color = ExploreUiTokens.SubtitleColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ExploreCenteredState(
    text: String,
    action: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        action()
        Spacer(Modifier.height(12.dp))
        Text(
            text = text,
            color = ExploreUiTokens.SubtitleColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun buildExploreTitle(sortMode: ExploreSortMode): String {
    return when (sortMode) {
        ExploreSortMode.HOT -> stringResource(Res.string.explore_sort_hot_title)
        ExploreSortMode.RECENT -> stringResource(Res.string.explore_sort_recent_title)
        ExploreSortMode.CONTROVERSIAL -> stringResource(Res.string.explore_sort_controversial_title)
    }
}
