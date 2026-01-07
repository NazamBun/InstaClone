package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Chip catégorie :
 * ✅ Pas d’icônes
 * ✅ Look dark
 */
@Composable
internal fun ExploreCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg: Color = if (selected) ExploreUiTokens.Accent else ExploreUiTokens.ChipBgNormal

    Surface(
        tonalElevation = 0.dp,
        shape = MaterialTheme.shapes.large,
        color = bg,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}