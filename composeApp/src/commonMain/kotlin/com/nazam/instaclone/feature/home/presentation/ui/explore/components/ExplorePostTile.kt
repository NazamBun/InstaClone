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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.cd_poll_image
import instaclone.composeapp.generated.resources.percent_value
import instaclone.composeapp.generated.resources.vs_title_format
import instaclone.composeapp.generated.resources.vspost_votes_count
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max

@Composable
internal fun ExplorePostTile(
    post: VsPost,
    onClick: (VsPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalVotes = max(post.totalVotesCount, 1)
    val leftRatio = post.leftVotesCount.toFloat() / totalVotes
    val rightRatio = post.rightVotesCount.toFloat() / totalVotes
    val leftPercent = (leftRatio * 100f).toInt()
    val rightPercent = (rightRatio * 100f).toInt()

    val imageUrl = if (post.leftVotesCount >= post.rightVotesCount) {
        post.leftImageUrl
    } else {
        post.rightImageUrl
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick(post) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NetworkImage(
                url = imageUrl,
                contentDescription = stringResource(Res.string.cd_poll_image),
                modifier = Modifier.fillMaxSize()
            )

            Box(
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
                    text = stringResource(
                        Res.string.vs_title_format,
                        post.leftLabel,
                        post.rightLabel
                    ),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(Res.string.vspost_votes_count, post.totalVotesCount),
                    color = ExploreUiTokens.SubtitleColor,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                PercentRow(post.leftLabel, leftPercent)
                ExploreVoteProgressBar(progress = leftRatio, height = 6.dp)

                Spacer(modifier = Modifier.height(6.dp))

                PercentRow(post.rightLabel, rightPercent)
                ExploreVoteProgressBar(progress = rightRatio, height = 6.dp)
            }
        }
    }
}

@Composable
private fun PercentRow(
    label: String,
    percent: Int
) {
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
