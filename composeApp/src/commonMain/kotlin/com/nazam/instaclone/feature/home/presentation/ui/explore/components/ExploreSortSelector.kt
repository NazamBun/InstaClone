package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExploreSortMode

/**
 * Petit choix de tri.
 * ✅ KMP friendly
 * ✅ Pas d'icônes
 * ✅ Réutilisable
 */
@Composable
fun ExploreSortSelector(
    selected: ExploreSortMode,
    onSelected: (ExploreSortMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExploreSortChip(
            label = "Hot",
            selected = selected == ExploreSortMode.HOT,
            onClick = { onSelected(ExploreSortMode.HOT) }
        )

        ExploreSortChip(
            label = "Recent",
            selected = selected == ExploreSortMode.RECENT,
            onClick = { onSelected(ExploreSortMode.RECENT) }
        )

        ExploreSortChip(
            label = "Controversé",
            selected = selected == ExploreSortMode.CONTROVERSIAL,
            onClick = { onSelected(ExploreSortMode.CONTROVERSIAL) }
        )
    }
}

@Composable
private fun ExploreSortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = ExploreUiTokens.Accent
    val bg = if (selected) accent else Color(0xFF14141A)
    val fg = Color.White

    Surface(
        tonalElevation = 0.dp,
        shape = MaterialTheme.shapes.large,
        color = bg,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}