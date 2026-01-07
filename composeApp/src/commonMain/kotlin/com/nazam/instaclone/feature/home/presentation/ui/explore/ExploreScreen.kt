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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.domain.model.VoteCategory
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreCategoryChip
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExplorePostTile
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreSortSelector
import com.nazam.instaclone.feature.home.presentation.ui.explore.components.ExploreUiTokens

/**
 * ExploreScreen (Découvrir)
 * - Catégories (chips)
 * - Tri (Hot / Recent / Controversé) en state local (V1)
 * - Grid 3 colonnes
 *
 * ✅ KMP friendly
 * ✅ Pas d’icônes
 */
@Composable
fun ExploreScreen(
    ui: HomeUiState,
    contentPadding: PaddingValues,
    onCategoryClick: (VoteCategory) -> Unit,
    onClearCategory: () -> Unit,
    onPostClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedId = ui.selectedCategoryId

    // ✅ V1 : state local
    var sortMode by remember { mutableStateOf(ExploreSortMode.HOT) }

    // ✅ Tri (pur)
    val sortedPosts: List<VsPost> = sortExplorePosts(
        posts = ui.posts,
        mode = sortMode
    )

    // ✅ Filtre catégorie
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
                text = "Découvrir",
                color = ExploreUiTokens.TitleColor,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Catégories
            LazyRow(
                contentPadding = PaddingValues(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ExploreCategoryChip(
                        label = "Tout",
                        selected = selectedId.isBlank(),
                        onClick = onClearCategory
                    )
                }

                items(VoteCategories.all) { category ->
                    ExploreCategoryChip(
                        label = category.label,
                        selected = category.id == selectedId,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ✅ Choix de tri
            ExploreSortSelector(
                selected = sortMode,
                onSelected = { sortMode = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            val title =
                if (selectedId.isBlank()) {
                    when (sortMode) {
                        ExploreSortMode.HOT -> "Hot (plus de votes)"
                        ExploreSortMode.RECENT -> "Recent (les plus récents)"
                        ExploreSortMode.CONTROVERSIAL -> "Controversé (votes serrés)"
                    }
                } else {
                    "Catégorie : ${VoteCategories.labelFor(selectedId)}"
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