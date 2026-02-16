package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp

/**
 * Barre de progression :
 * - fond (track) sombre
 * - remplissage en dégradé jaune -> orange -> rouge
 *
 * ✅ KMP friendly (Canvas)
 */
@Composable
internal fun ExploreVoteProgressBar(
    progress: Float,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val safe = progress.coerceIn(0f, 1f)

    val gradientBrush = Brush.horizontalGradient(colors = ExploreUiTokens.ProgressGradient)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val radius = CornerRadius(x = size.height / 2f, y = size.height / 2f)

        // Track
        drawRoundRect(
            color = ExploreUiTokens.TrackColor,
            size = size,
            cornerRadius = radius
        )

        // Progress
        val width = size.width * safe
        if (width > 0f) {
            drawRoundRect(
                brush = gradientBrush,
                size = androidx.compose.ui.geometry.Size(width = width, height = size.height),
                cornerRadius = radius
            )
        }
    }
}