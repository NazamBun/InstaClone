package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.feature.home.domain.model.VsPost

data class HomeUiState(
    val isLoading: Boolean = false,

    // 🔒 id du post en cours de vote (null = aucun vote en cours)
    val votingPostId: String? = null,

    val posts: List<VsPost> = emptyList(),
    val errorMessage: String? = null
)