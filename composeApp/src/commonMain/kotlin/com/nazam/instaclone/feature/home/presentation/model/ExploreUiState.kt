package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExploreSortMode

data class ExploreUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val posts: List<VsPost> = emptyList(),
    val sortMode: ExploreSortMode = ExploreSortMode.HOT,
    val error: UiText? = null
)
