package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp

@Composable
internal fun ExploreVoteProgressBar(
    progress: Float,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    val brush = Brush.horizontalGradient(ExploreUiTokens.ProgressGradient)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val radius = CornerRadius(size.height / 2f, size.height / 2f)

        drawRoundRect(
            color = ExploreUiTokens.TrackColor,
            size = size,
            cornerRadius = radius
        )

        val progressWidth = size.width * safeProgress
        if (progressWidth > 0f) {
            drawRoundRect(
                brush = brush,
                size = Size(progressWidth, size.height),
                cornerRadius = radius
            )
        }
    }
}
