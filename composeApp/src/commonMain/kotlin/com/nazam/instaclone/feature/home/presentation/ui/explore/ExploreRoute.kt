package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.home.presentation.viewmodel.ExploreViewModel
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import org.koin.compose.koinInject

@Composable
fun ExploreRoute(
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues
) {
    val homeViewModel: HomeViewModel = koinInject()
    val exploreViewModel: ExploreViewModel = koinInject()

    val homeUi by homeViewModel.uiState.collectAsState()
    val exploreUi by exploreViewModel.uiState.collectAsState()

    ExploreScreen(
        selectedCategoryId = homeUi.selectedCategoryId,
        exploreUi = exploreUi,
        contentPadding = contentPadding,
        onUniverseClick = homeViewModel::onUniverseSelected,
        onCategoryClick = homeViewModel::onExploreCategoryClicked,
        onClearCategory = homeViewModel::onExploreClearCategory,
        onSortSelected = exploreViewModel::onSortModeSelected,
        onRetry = exploreViewModel::load,
        onLoadMore = exploreViewModel::loadMore,
        onPostClick = { post ->
            ExplorePagerStore.open(
                categoryId = post.category,
                startPostId = post.id
            )
            onNavigate(Screen.ExplorePager)
        }
    )
}
