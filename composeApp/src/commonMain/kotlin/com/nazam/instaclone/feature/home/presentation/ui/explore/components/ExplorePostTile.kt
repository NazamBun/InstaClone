package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import kotlin.math.max

/**
 * Tuile carrée (grid) :
 * - image gagnante
 * - titre "A vs B"
 * - 2 lignes label + % (petit)
 * - 2 barres de progression
 *
 * ✅ Pas d’icônes
 * ✅ KMP friendly
 */
@Composable
internal fun ExplorePostTile(
    post: VsPost,
    onClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
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
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick(post) }
    ) {
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {

            NetworkImage(
                url = winnerUrl,
                contentDescription = "Image du sondage",
                modifier = Modifier.fillMaxSize()
            )

            // voile sombre pour lire le texte
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ExploreUiTokens.OverlayOnImage)
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

                LabelPercentRow(label = post.leftLabel, percent = leftPct)
                ExploreVoteProgressBar(progress = leftRatio, height = 6.dp)

                Spacer(modifier = Modifier.height(6.dp))

                LabelPercentRow(label = post.rightLabel, percent = rightPct)
                ExploreVoteProgressBar(progress = rightRatio, height = 6.dp)
            }
        }
    }
}

/**
 * Petite ligne : "Messi 52%"
 */
@Composable
private fun LabelPercentRow(
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
            color = ExploreUiTokens.SubtitleColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}