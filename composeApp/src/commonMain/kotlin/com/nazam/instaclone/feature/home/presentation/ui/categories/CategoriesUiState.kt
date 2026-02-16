package com.nazam.instaclone.feature.home.presentation.ui.categories

import com.nazam.instaclone.feature.home.domain.model.VoteCategory

data class CategoriesUiState(
    val items: List<VoteCategory> = emptyList()
)