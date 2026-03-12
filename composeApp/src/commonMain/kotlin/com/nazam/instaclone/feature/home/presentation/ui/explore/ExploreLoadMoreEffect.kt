package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow

@Composable
internal fun ExploreLoadMoreEffect(
    gridState: LazyGridState,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    endReached: Boolean,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(gridState, isLoading, isLoadingMore, endReached) {
        snapshotFlow {
            val info = gridState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            total > 0 && lastVisible >= total - 6
        }.collect { nearEnd ->
            if (nearEnd && !isLoading && !isLoadingMore && !endReached) {
                onLoadMore()
            }
        }
    }
}
