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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.domain.model.VoteCategory
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.ExploreUiState
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreCategoryChip
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExplorePostTile
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreSortSelector
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreUiTokens
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.explore_all
import instaclone.composeapp.generated.resources.explore_category_prefix
import instaclone.composeapp.generated.resources.explore_sort_controversial_title
import instaclone.composeapp.generated.resources.explore_sort_hot_title
import instaclone.composeapp.generated.resources.explore_sort_recent_title
import instaclone.composeapp.generated.resources.explore_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExploreScreen(
    homeUi: HomeUiState,
    exploreUi: ExploreUiState,
    contentPadding: PaddingValues,
    onCategoryClick: (VoteCategory) -> Unit,
    onClearCategory: () -> Unit,
    onSortSelected: (ExploreSortMode) -> Unit,
    onPostClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedId = homeUi.selectedCategoryId
    val sortMode = exploreUi.sortMode

    val sortedPosts: List<VsPost> = sortExplorePosts(
        posts = homeUi.posts,
        mode = sortMode
    )

    val visiblePosts: List<VsPost> =
        if (selectedId.isBlank()) sortedPosts
        else sortedPosts.filter { it.category == selectedId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ExploreUiTokens.ScreenBackground)
            .padding(contentPadding)
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

            LazyRow(
                contentPadding = PaddingValues(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ExploreCategoryChip(
                        label = stringResource(Res.string.explore_all),
                        selected = selectedId.isBlank(),
                        onClick = onClearCategory
                    )
                }

                items(VoteCategories.all) { category ->
                    ExploreCategoryChip(
                        label = category.label.asString(),
                        selected = category.id == selectedId,
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

            val title: String =
                if (selectedId.isBlank()) {
                    when (sortMode) {
                        ExploreSortMode.HOT -> stringResource(Res.string.explore_sort_hot_title)
                        ExploreSortMode.RECENT -> stringResource(Res.string.explore_sort_recent_title)
                        ExploreSortMode.CONTROVERSIAL -> stringResource(Res.string.explore_sort_controversial_title)
                    }
                } else {
                    stringResource(Res.string.explore_category_prefix) + " " +
                        VoteCategories.labelFor(selectedId).asString()
                }

            Text(
                text = title,
                color = ExploreUiTokens.SubtitleColor,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(visiblePosts) { post ->
                    ExplorePostTile(
                        post = post,
                        onClick = onPostClick
                    )
                }
            }
        }
    }
}
