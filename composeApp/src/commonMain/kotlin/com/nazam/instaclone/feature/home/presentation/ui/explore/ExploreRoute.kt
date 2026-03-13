package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.home.presentation.viewmodel.ExploreViewModel
import org.koin.compose.koinInject

@Composable
fun ExploreRoute(
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues
) {
    val viewModel: ExploreViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    ExploreScreen(
        exploreUi = ui,
        contentPadding = contentPadding,
        onSortSelected = viewModel::onSortModeSelected,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onRetry = viewModel::load,
        onLoadMore = viewModel::loadMore,
        onPostClick = { post ->
            ExplorePagerStore.open(categoryId = post.category, startPostId = post.id)
            onNavigate(Screen.ExplorePager)
        }
    )
}
