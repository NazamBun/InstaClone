package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.cd_poll_image
import instaclone.composeapp.generated.resources.explore_sort_hot
import instaclone.composeapp.generated.resources.percent_value
import instaclone.composeapp.generated.resources.vs_title_format
import instaclone.composeapp.generated.resources.vspost_votes_count
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max

@Composable
internal fun ExplorePostTile(
    post: VsPost,
    style: ExploreTileStyle,
    onClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalVotes = max(post.totalVotesCount, 1)
    val leftRatio = post.leftVotesCount.toFloat() / totalVotes
    val rightRatio = post.rightVotesCount.toFloat() / totalVotes
    val leftPercent = (leftRatio * 100f).toInt()
    val rightPercent = (rightRatio * 100f).toInt()

    val winningImage = if (post.leftVotesCount >= post.rightVotesCount) {
        post.leftImageUrl
    } else {
        post.rightImageUrl
    }

    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(exploreTileAspectRatio(style))
            .clickable { onClick(post) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NetworkImage(
                url = winningImage,
                contentDescription = stringResource(Res.string.cd_poll_image),
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                ExploreUiTokens.OverlayOnImage,
                                Color(0xCC000000)
                            )
                        )
                    )
            )

            if (style == ExploreTileStyle.FEATURED) {
                HotBadge(modifier = Modifier.align(Alignment.TopStart))
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = stringResource(
                        Res.string.vs_title_format,
                        post.leftLabel,
                        post.rightLabel
                    ),
                    color = Color.White,
                    style = if (style == ExploreTileStyle.FEATURED) {
                        MaterialTheme.typography.titleSmall
                    } else {
                        MaterialTheme.typography.bodyMedium
                    },
                    fontWeight = FontWeight.SemiBold,
                    maxLines = if (style == ExploreTileStyle.FEATURED) 2 else 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(Res.string.vspost_votes_count, post.totalVotesCount),
                    color = ExploreUiTokens.SubtitleColor,
                    style = MaterialTheme.typography.bodySmall
                )

                if (style == ExploreTileStyle.STANDARD) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PercentRow(label = post.leftLabel, percent = leftPercent)
                    ExploreVoteProgressBar(progress = leftRatio, height = 6.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    PercentRow(label = post.rightLabel, percent = rightPercent)
                    ExploreVoteProgressBar(progress = rightRatio, height = 6.dp)
                }
            }
        }
    }
}

@Composable
private fun HotBadge(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.explore_sort_hot),
        color = ExploreUiTokens.SearchTagColor,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .padding(10.dp)
            .background(
                color = ExploreUiTokens.SearchTagBackground,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun PercentRow(label: String, percent: Int) {
    Row(
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
            text = stringResource(Res.string.percent_value, percent),
            color = ExploreUiTokens.SubtitleColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
