package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.feature.home.presentation.ui.explore.ExploreSortMode

/**
 * State dédié à Explore.
 * ✅ Séparé de HomeUiState (clean)
 * ✅ Simple à tester
 */
data class ExploreUiState(
    val sortMode: ExploreSortMode = ExploreSortMode.HOT
)