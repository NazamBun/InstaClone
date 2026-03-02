package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.ui.graphics.Color

/**
 * Petits "tokens UI" :
 * - couleurs et styles de base
 * - évite de répéter des valeurs partout
 *
 * ✅ KMP friendly
 */
internal object ExploreUiTokens {
    val ScreenBackground = Color(0xFF050509)

    val Accent = Color(0xFF2F5BFF)

    val ChipBgNormal = Color(0xFF14141A)

    val TitleColor = Color.White
    val SubtitleColor = Color(0xFFDDDDDD)

    val OverlayOnImage = Color(0x66000000)

    val TrackColor = Color(0xFF1B1B22)

    val ProgressGradient = listOf(
        Color(0xFFFFD54F), // jaune
        Color(0xFFFF9800), // orange
        Color(0xFFFF1744)  // rouge vif
    )
}