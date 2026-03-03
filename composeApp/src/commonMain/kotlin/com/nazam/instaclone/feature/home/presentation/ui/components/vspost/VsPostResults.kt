package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    modifier: Modifier = Modifier
) {
    val total = post.totalVotesCount.coerceAtLeast(1)
    val leftPercent = ((post.leftVotesCount * 100f) / total).coerceIn(0f, 100f)
    val rightPercent = ((post.rightVotesCount * 100f) / total).coerceIn(0f, 100f)

    val leftPercentInt = leftPercent.roundToInt()
    val rightPercentInt = rightPercent.roundToInt()

    val leftRatio = (leftPercent / 100f).coerceIn(0f, 1f)
    val rightRatio = (rightPercent / 100f).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp + extraBottomPadding)
            .alpha(resultsAlpha)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(post.leftLabel, color = Color.White)
                Text(
                    text = stringResource(Res.string.vspost_votes_count, post.leftVotesCount),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.percent_value, leftPercentInt),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))
                PercentBar(
                    ratio = leftRatio,
                    showFlame = leftPercentInt >= 100,
                    reverse = true,
                    fill = Brush.horizontalGradient(listOf(Color(0xFF7B61FF), Color(0xFFB95CFF)))
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(post.rightLabel, color = Color.White)
                Text(
                    text = stringResource(Res.string.vspost_votes_count, post.rightVotesCount),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.percent_value, rightPercentInt),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))
                PercentBar(
                    ratio = rightRatio,
                    showFlame = rightPercentInt >= 100,
                    reverse = false,
                    fill = Brush.horizontalGradient(listOf(Color(0xFFFF9F3F), Color(0xFF2F5BFF)))
                )
            }
        }
    }
}

@Composable
private fun PercentBar(
    ratio: Float,
    showFlame: Boolean,
    reverse: Boolean,
    fill: Brush
) {
    val t = androidx.compose.animation.core.animateFloatAsState(
        targetValue = ratio.coerceIn(0f, 1f),
        animationSpec = tween(450),
        label = "barRatio"
    ).value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(Color(0x33FFFFFF), RoundedCornerShape(50))
    ) {
        val inner = Modifier
            .fillMaxHeight()
            .fillMaxWidth(t)
            .background(fill, RoundedCornerShape(50))

        if (reverse) Box(modifier = inner.align(Alignment.CenterEnd)) else Box(modifier = inner)

        if (showFlame) {
            Flame(
                modifier = Modifier
                    .align(if (reverse) Alignment.CenterStart else Alignment.CenterEnd)
                    .offset(y = (-14).dp)
            )
        }
    }
}

@Composable
private fun Flame(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "flame")
    val s by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "flameScale"
    )

    Text(
        text = "🔥",
        fontSize = 12.sp,
        modifier = modifier.scale(s).alpha(0.95f)
    )
}
