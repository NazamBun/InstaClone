package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
                    text = stringResource(Res.string.percent_value, leftPercent.roundToInt()),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))
                PercentBar(
                    ratio = leftRatio,
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
                    text = stringResource(Res.string.percent_value, rightPercent.roundToInt()),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))
                PercentBar(
                    ratio = rightRatio,
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
    reverse: Boolean,
    fill: Brush
) {
    // Animation du remplissage
    val t = androidx.compose.animation.core.animateFloatAsState(
        targetValue = ratio.coerceIn(0f, 1f),
        animationSpec = tween(450),
        label = "barRatio"
    ).value

    BoxWithConstraints(
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

        // 🔥 seulement à 100%
        FlameAtEnd(
            show = t >= 0.999f,
            progress = t,
            reverse = reverse,
            maxWidthDp = this.maxWidth
        )
    }
}

@Composable
private fun FlameAtEnd(
    show: Boolean,
    progress: Float,
    reverse: Boolean,
    maxWidthDp: Dp
) {
    if (!show) return

    // Petit "bounce" discret
    val infinite = rememberInfiniteTransition(label = "flame")
    val s by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(420),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )

    // Position du bout de la barre
    val x = if (reverse) maxWidthDp * (1f - progress) else maxWidthDp * progress

    Text(
        text = "🔥",
        fontSize = 12.sp,
        modifier = Modifier
            .offset(x = x - 8.dp, y = (-16).dp) // 8dp ≈ demi-largeur emoji
            .scale(s)
            .alpha(0.95f)
    )
}
