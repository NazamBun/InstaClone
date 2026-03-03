package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import kotlin.math.roundToInt

@Composable
internal fun ExploreVoteProgressBar(
    progress: Float,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val safe = progress.coerceIn(0f, 1f)
    val percentInt = (safe * 100f).roundToInt()
    val gradientBrush = Brush.horizontalGradient(colors = ExploreUiTokens.ProgressGradient)

    Box(modifier = modifier.fillMaxWidth().height(height)) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val radius = CornerRadius(x = size.height / 2f, y = size.height / 2f)

            drawRoundRect(
                color = ExploreUiTokens.TrackColor,
                size = size,
                cornerRadius = radius
            )

            val w = size.width * safe
            if (w > 0f) {
                drawRoundRect(
                    brush = gradientBrush,
                    size = androidx.compose.ui.geometry.Size(width = w, height = size.height),
                    cornerRadius = radius
                )
            }
        }

        if (percentInt >= 100) {
            Flame(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(y = (-14).dp)
            )
        }
    }
}

@Composable
private fun Flame(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "flameExplore")
    val s by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "flameScaleExplore"
    )

    Text(
        text = "🔥",
        fontSize = 12.sp,
        modifier = modifier.scale(s).alpha(0.95f)
    )
}
