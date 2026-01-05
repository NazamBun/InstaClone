package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.domain.model.VoteCategory
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import kotlin.math.max

/**
 * ExploreScreen (Découvrir)
 * - En haut : catégories (chips)
 * - En bas : grid 3 colonnes (carrés)
 * - Chaque tuile : image gagnante + titre + 2 barres de progression (dégradé)
 *
 * ✅ KMP friendly (Canvas)
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

    // Hot = tri par votes (plus grand -> plus petit)
    val hotPosts: List<VsPost> = ui.posts.sortedByDescending { it.totalVotesCount }

    val visiblePosts: List<VsPost> =
        if (selectedId.isBlank()) hotPosts else hotPosts.filter { it.category == selectedId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050509))
            .padding(contentPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Découvrir",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chips catégories
            LazyRow(
                contentPadding = PaddingValues(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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

            Spacer(modifier = Modifier.height(14.dp))

            val title =
                if (selectedId.isBlank()) "Hot (plus de votes)"
                else "Catégorie : ${VoteCategories.labelFor(selectedId)}"

            Text(
                text = title,
                color = Color(0xFFDDDDDD),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ✅ GRID 3 colonnes (carrés)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(visiblePosts) { post ->
                    ExplorePostTile(
                        post = post,
                        onClick = { onPostClick(post) }
                    )
                }
            }
        }
    }
}

/**
 * Chip simple, look dark + pro.
 * ✅ Pas d’icônes
 */
@Composable
private fun ExploreChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = Color(0xFFFF4EB8)
    val bg = if (selected) accent else Color(0xFF14141A)

    Surface(
        tonalElevation = 0.dp,
        shape = MaterialTheme.shapes.large,
        color = bg,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

/**
 * Tuile carrée :
 * - image gagnante
 * - titre du sondage
 * - 2 barres de progression (dégradé jaune -> orange -> rouge) + % petit
 */
@Composable
private fun ExplorePostTile(
    post: VsPost,
    onClick: () -> Unit
) {
    val totalVotes = max(post.totalVotesCount, 1)

    val leftRatio = post.leftVotesCount.toFloat() / totalVotes.toFloat()
    val rightRatio = post.rightVotesCount.toFloat() / totalVotes.toFloat()

    val leftPct = (leftRatio * 100f).toInt()
    val rightPct = (rightRatio * 100f).toInt()

    val winnerUrl =
        if (post.leftVotesCount >= post.rightVotesCount) post.leftImageUrl else post.rightImageUrl

    val title = "${post.leftLabel} vs ${post.rightLabel}"

    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NetworkImage(
                url = winnerUrl,
                contentDescription = "Image du sondage",
                modifier = Modifier.fillMaxSize()
            )

            // Voile sombre pour lire le texte
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                RowLabelPercent(label = post.leftLabel, percent = leftPct)
                GradientProgressBar(progress = leftRatio, height = 6.dp)

                Spacer(modifier = Modifier.height(6.dp))

                RowLabelPercent(label = post.rightLabel, percent = rightPct)
                GradientProgressBar(progress = rightRatio, height = 6.dp)
            }
        }
    }
}

/**
 * Petite ligne : "Messi 52%"
 */
@Composable
private fun RowLabelPercent(
    label: String,
    percent: Int
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$percent%",
            color = Color(0xFFDDDDDD),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * Barre de progression :
 * - Fond sombre (track)
 * - Remplissage avec dégradé (jaune -> orange -> rouge)
 *
 * ✅ KMP friendly (Canvas)
 */
@Composable
private fun GradientProgressBar(
    progress: Float,
    height: Dp
) {
    val safe = progress.coerceIn(0f, 1f)

    val trackColor = Color(0xFF1B1B22)
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFFD54F), // jaune
            Color(0xFFFF9800), // orange
            Color(0xFFFF1744)  // rouge vif
        )
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val radius = CornerRadius(x = size.height / 2f, y = size.height / 2f)

        // Track (fond)
        drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = radius
        )

        // Progress (remplissage)
        val w = size.width * safe
        if (w > 0f) {
            drawRoundRect(
                brush = gradient,
                size = androidx.compose.ui.geometry.Size(width = w, height = size.height),
                cornerRadius = radius
            )
        }
    }
}