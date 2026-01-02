package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.domain.model.VoteCategory
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState

/**
 * ExploreScreen :
 * - En haut : catégories (chips)
 * - En bas : posts "Hot" (tri votes) ou filtrés par catégorie
 *
 * ✅ Pas d'icônes
 */
@Composable
fun ExploreScreen(
    ui: HomeUiState,
    onBackHome: () -> Unit,
    onCategoryClick: (VoteCategory) -> Unit,
    onClearCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedId = ui.selectedCategoryId

    // Hot = tri par votes (plus grand -> plus petit)
    val hotPosts: List<VsPost> = ui.posts.sortedByDescending { it.totalVotesCount }

    val visiblePosts: List<VsPost> =
        if (selectedId.isBlank()) hotPosts else hotPosts.filter { it.category == selectedId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Découvrir",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Chips catégories
        LazyRow(
            contentPadding = PaddingValues(end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Un chip "Tout"
            item {
                ExploreChip(
                    label = "Tout",
                    selected = selectedId.isBlank(),
                    onClick = onClearCategory
                )
            }

            items(VoteCategories.all) { category ->
                ExploreChip(
                    label = category.label,
                    selected = category.id == selectedId,
                    onClick = { onCategoryClick(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val title = if (selectedId.isBlank()) "Hot (plus de votes)"
        else "Catégorie : ${VoteCategories.labelFor(selectedId)}"

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(visiblePosts) { post ->
                ExplorePostCard(post = post)
            }
        }
    }
}

/**
 * Chip simple (style pro sans icônes).
 */
@Composable
private fun ExploreChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large,
        color = bg,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

/**
 * Carte post simple :
 * - question
 * - catégorie
 * - votes
 */
@Composable
private fun ExplorePostCard(
    post: VsPost
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = post.question,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Catégorie : ${VoteCategories.labelFor(post.category)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Votes : ${post.totalVotesCount} (G:${post.leftVotesCount} / D:${post.rightVotesCount})",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}