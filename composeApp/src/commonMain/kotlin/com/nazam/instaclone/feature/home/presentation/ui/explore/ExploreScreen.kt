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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.core.universe.Universe
import com.nazam.instaclone.core.universe.UniverseStore
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.domain.model.VoteCategory
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.ExploreUiState
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreCategoryChip
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExplorePostTile
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreSortSelector
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreUiTokens
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.action_retry
import instaclone.composeapp.generated.resources.explore_all
import instaclone.composeapp.generated.resources.explore_category_prefix
import instaclone.composeapp.generated.resources.explore_empty_category
import instaclone.composeapp.generated.resources.explore_load_error
import instaclone.composeapp.generated.resources.explore_loading
import instaclone.composeapp.generated.resources.explore_sort_controversial_title
import instaclone.composeapp.generated.resources.explore_sort_hot_title
import instaclone.composeapp.generated.resources.explore_sort_recent_title
import instaclone.composeapp.generated.resources.explore_title
import instaclone.composeapp.generated.resources.explore_universe_title
import instaclone.composeapp.generated.resources.universe_football
import instaclone.composeapp.generated.resources.universe_global
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExploreScreen(
    selectedCategoryId: String,
    exploreUi: ExploreUiState,
    contentPadding: PaddingValues,
    onUniverseClick: (Universe) -> Unit,
    onCategoryClick: (VoteCategory) -> Unit,
    onClearCategory: () -> Unit,
    onSortSelected: (ExploreSortMode) -> Unit,
    onRetry: () -> Unit,
    onPostClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val universe by UniverseStore.universe.collectAsState()
    val sortedPosts = sortExplorePosts(exploreUi.posts, exploreUi.sortMode)
    val visiblePosts = if (selectedCategoryId.isBlank()) {
        sortedPosts
    } else {
        sortedPosts.filter { it.category == selectedCategoryId }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ExploreUiTokens.ScreenBackground)
            .padding(contentPadding)
    ) {
        when {
            exploreUi.isLoading -> {
                ExploreCenteredState(
                    text = stringResource(Res.string.explore_loading)
                ) {
                    CircularProgressIndicator()
                }
            }

            exploreUi.error != null -> {
                ExploreCenteredState(
                    text = exploreUi.error.asString()
                ) {
                    Button(onClick = onRetry) {
                        Text(stringResource(Res.string.action_retry))
                    }
                }
            }

            else -> {
                ExploreContent(
                    selectedCategoryId = selectedCategoryId,
                    sortMode = exploreUi.sortMode,
                    universe = universe,
                    posts = visiblePosts,
                    onUniverseClick = onUniverseClick,
                    onCategoryClick = onCategoryClick,
                    onClearCategory = onClearCategory,
                    onSortSelected = onSortSelected,
                    onPostClick = onPostClick
                )
            }
        }
    }
}

@Composable
private fun ExploreContent(
    selectedCategoryId: String,
    sortMode: ExploreSortMode,
    universe: Universe,
    posts: List<VsPost>,
    onUniverseClick: (Universe) -> Unit,
    onCategoryClick: (VoteCategory) -> Unit,
    onClearCategory: () -> Unit,
    onSortSelected: (ExploreSortMode) -> Unit,
    onPostClick: (VsPost) -> Unit
) {
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

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.explore_universe_title),
            color = ExploreUiTokens.SubtitleColor,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                ExploreCategoryChip(
                    label = stringResource(Res.string.universe_football),
                    selected = universe == Universe.FOOTBALL,
                    onClick = { onUniverseClick(Universe.FOOTBALL) }
                )
            }
            item {
                ExploreCategoryChip(
                    label = stringResource(Res.string.universe_global),
                    selected = universe == Universe.GLOBAL,
                    onClick = { onUniverseClick(Universe.GLOBAL) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ExploreCategoryChip(
                    label = stringResource(Res.string.explore_all),
                    selected = selectedCategoryId.isBlank(),
                    onClick = onClearCategory
                )
            }

            items(VoteCategories.all) { category ->
                ExploreCategoryChip(
                    label = category.label.asString(),
                    selected = category.id == selectedCategoryId,
                    onClick = { onCategoryClick(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ExploreSortSelector(
            selected = sortMode,
            onSelected = onSortSelected
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = buildExploreTitle(selectedCategoryId, sortMode),
            color = ExploreUiTokens.SubtitleColor,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (posts.isEmpty()) {
            ExploreEmptyState()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts) { post ->
                    ExplorePostTile(
                        post = post,
                        onClick = onPostClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.explore_empty_category),
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
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        action()
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = text,
            color = ExploreUiTokens.SubtitleColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun buildExploreTitle(
    selectedCategoryId: String,
    sortMode: ExploreSortMode
): String {
    return if (selectedCategoryId.isBlank()) {
        when (sortMode) {
            ExploreSortMode.HOT -> stringResource(Res.string.explore_sort_hot_title)
            ExploreSortMode.RECENT -> stringResource(Res.string.explore_sort_recent_title)
            ExploreSortMode.CONTROVERSIAL -> stringResource(Res.string.explore_sort_controversial_title)
        }
    } else {
        stringResource(Res.string.explore_category_prefix) +
            " " +
            VoteCategories.labelFor(selectedCategoryId).asString()
    }
}
