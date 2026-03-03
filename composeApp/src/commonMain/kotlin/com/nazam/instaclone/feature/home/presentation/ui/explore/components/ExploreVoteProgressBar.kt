package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale

/**
 * Barre de progression + 🔥 quand progress = 100%
 * ✅ KMP friendly
 */
@Composable
internal fun ExploreVoteProgressBar(
    progress: Float,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val safe = progress.coerceIn(0f, 1f)
    val gradientBrush = Brush.horizontalGradient(colors = ExploreUiTokens.ProgressGradient)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val radius = CornerRadius(x = size.height / 2f, y = size.height / 2f)

            // Track
            drawRoundRect(
                color = ExploreUiTokens.TrackColor,
                size = size,
                cornerRadius = radius
            )

            // Progress
            val w = size.width * safe
            if (w > 0f) {
                drawRoundRect(
                    brush = gradientBrush,
                    size = androidx.compose.ui.geometry.Size(width = w, height = size.height),
                    cornerRadius = radius
                )
            }
        }

        FlameAtEnd(show = safe >= 0.999f, progress = safe, maxWidthDp = this.maxWidth)
    }
}

@Composable
private fun FlameAtEnd(
    show: Boolean,
    progress: Float,
    maxWidthDp: Dp
) {
    if (!show) return

    val infinite = rememberInfiniteTransition(label = "flameExplore")
    val s by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "flameScaleExplore"
    )

    val x = maxWidthDp * progress

    Text(
        text = "🔥",
        fontSize = 12.sp,
        modifier = Modifier
            .offset(x = x - 8.dp, y = (-14).dp)
            .scale(s)
            .alpha(0.95f)
    )
}
