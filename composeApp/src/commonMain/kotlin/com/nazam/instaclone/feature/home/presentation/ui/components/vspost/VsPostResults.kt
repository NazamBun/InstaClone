package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.percent_value
import instaclone.composeapp.generated.resources.vspost_votes_count
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun VsPostResults(
    post: VsPost,
    resultsAlpha: Float,
    extraBottomPadding: Dp,
    modifier: Modifier = Modifier,
    onLeftReached100EndInWindow: (Offset) -> Unit = {},
    onRightReached100EndInWindow: (Offset) -> Unit = {}
) {
    val total = post.totalVotesCount.coerceAtLeast(1)
    val leftPercent = ((post.leftVotesCount * 100f) / total).coerceIn(0f, 100f)
    val rightPercent = ((post.rightVotesCount * 100f) / total).coerceIn(0f, 100f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 18.dp + extraBottomPadding)
            .alpha(resultsAlpha),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard(
            label = post.leftLabel,
            votes = post.leftVotesCount,
            percent = leftPercent.roundToInt(),
            reverse = true,
            fill = Brush.horizontalGradient(
                listOf(Color(0xFF7B61FF), Color(0xFFB95CFF))
            ),
            modifier = Modifier.weight(1f),
            alignEnd = false,
            onReached100EndInWindow = onLeftReached100EndInWindow
        )

        ResultCard(
            label = post.rightLabel,
            votes = post.rightVotesCount,
            percent = rightPercent.roundToInt(),
            reverse = false,
            fill = Brush.horizontalGradient(
                listOf(Color(0xFFFF9F3F), Color(0xFF2F5BFF))
            ),
            modifier = Modifier.weight(1f),
            alignEnd = true,
            onReached100EndInWindow = onRightReached100EndInWindow
        )
    }
}

@Composable
private fun ResultCard(
    label: String,
    votes: Int,
    percent: Int,
    reverse: Boolean,
    fill: Brush,
    modifier: Modifier,
    alignEnd: Boolean,
    onReached100EndInWindow: (Offset) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.22f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
        ) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(Res.string.vspost_votes_count, votes),
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = stringResource(Res.string.percent_value, percent),
                color = Color.White,
                style = MaterialTheme.typography.titleSmall
            )

            VsPostPercentBar(
                ratio = percent / 100f,
                reverse = reverse,
                fill = fill,
                onReached100EndInWindow = onReached100EndInWindow
            )
        }
    }
}
