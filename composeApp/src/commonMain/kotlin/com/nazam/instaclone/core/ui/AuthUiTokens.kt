package com.nazam.instaclone.core.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Petits tokens UI pour rester cohérent partout.
 * (Même ambiance que le feed : noir + accent rose)
 */
object AuthUiTokens {
    val Background = Color(0xFF050509)
    val Card = Color(0xFF0B0B10)
    val Field = Color(0xFF101018)

    val TextPrimary = Color.White
    val TextSecondary = Color(0xB3FFFFFF)

    val Accent = Color(0xFF2F5BFF)

    val CardRadius = 24.dp

    fun backgroundBrush(): Brush =
        Brush.verticalGradient(
            listOf(
                Background,
                Color(0xFF070712),
                Background
            )
        )
}
