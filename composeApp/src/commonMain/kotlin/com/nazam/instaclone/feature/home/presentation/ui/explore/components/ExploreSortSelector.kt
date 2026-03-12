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
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.explore_sort_controversial
import instaclone.composeapp.generated.resources.explore_sort_hot
import instaclone.composeapp.generated.resources.explore_sort_recent
import org.jetbrains.compose.resources.stringResource

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
        SortChip(
            label = stringResource(Res.string.explore_sort_hot),
            selected = selected == ExploreSortMode.HOT,
            onClick = { onSelected(ExploreSortMode.HOT) }
        )
        SortChip(
            label = stringResource(Res.string.explore_sort_recent),
            selected = selected == ExploreSortMode.RECENT,
            onClick = { onSelected(ExploreSortMode.RECENT) }
        )
        SortChip(
            label = stringResource(Res.string.explore_sort_controversial),
            selected = selected == ExploreSortMode.CONTROVERSIAL,
            onClick = { onSelected(ExploreSortMode.CONTROVERSIAL) }
        )
    }
}

@Composable
private fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) ExploreUiTokens.Accent else ExploreUiTokens.ChipBgNormal,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 0.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
