package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.feature.home.domain.model.VsPost

data class HomeUiState(
    val isLoading: Boolean = false,
    val isVoting: Boolean = false, // 🔒 verrou de vote
    val posts: List<VsPost> = emptyList(),
    val errorMessage: String? = null
)