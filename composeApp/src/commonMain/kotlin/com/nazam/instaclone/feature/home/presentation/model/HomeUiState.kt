package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.feature.home.domain.model.VsPost

data class HomeUiState(
    val isLoading: Boolean = false,

    // ✅ Session
    val isLoggedIn: Boolean = false,

    // 🔒 id du post en cours de vote
    val votingPostId: String? = null,

    val posts: List<VsPost> = emptyList(),

    // 🔔 Snackbar
    val snackbarMessage: String? = null,
    val snackbarActionLabel: String? = null,
    val shouldOpenLogin: Boolean = false
)