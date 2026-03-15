package com.nazam.instaclone.feature.home.presentation.ui.explore.components

internal enum class ExploreTileStyle {
    STANDARD,
    FEATURED
}

internal fun exploreTileStyleAt(index: Int): ExploreTileStyle {
    return when {
        index == 0 -> ExploreTileStyle.FEATURED
        index > 0 && index % 8 == 0 -> ExploreTileStyle.FEATURED
        else -> ExploreTileStyle.STANDARD
    }
}

internal fun exploreTileSpan(style: ExploreTileStyle): Int {
    return when (style) {
        ExploreTileStyle.FEATURED -> 3
        ExploreTileStyle.STANDARD -> 1
    }
}

internal fun exploreTileAspectRatio(style: ExploreTileStyle): Float {
    return when (style) {
        ExploreTileStyle.FEATURED -> 2.05f
        ExploreTileStyle.STANDARD -> 1f
    }
}
