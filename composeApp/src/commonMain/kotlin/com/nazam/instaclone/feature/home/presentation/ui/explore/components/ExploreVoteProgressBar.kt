package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
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

    var showFlame by remember { mutableStateOf(false) }
    LaunchedEffect(percentInt) {
        if (percentInt >= 100) {
            showFlame = true
            delay(2200)
            showFlame = false
        }
    }

    // ✅ Canvas d’abord, flamme après (donc au-dessus)
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

        AnimatedVisibility(
            visible = showFlame,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(260))
        ) {
            Flame(
                modifier = Modifier
                    .zIndex(999f)
                    .align(Alignment.CenterEnd)
                    .offset(y = (-20).dp)
            )
        }
    }
}

@Composable
private fun Flame(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "flameExplorePulse")
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "flameExploreScale"
    )

    Text(
        text = "🔥",
        fontSize = 20.sp,
        modifier = modifier.scale(pulse).alpha(0.98f)
    )
}
