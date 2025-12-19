package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.feature.home.domain.model.VsPost

data class HomeUiState(
    val isLoading: Boolean = false,

    // ✅ Session
    val isLoggedIn: Boolean = false,

    // 🔒 id du post en cours de vote
    val votingPostId: String? = null,

    val posts: List<VsPost> = emptyList(),

    // ✅ POPUP (Dialog)
    val dialogMessage: String? = null,
    val dialogConfirmLabel: String? = null, // ex: "Se connecter"
    val dialogShouldOpenLogin: Boolean = false
)