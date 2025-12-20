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
    val dialogConfirmLabel: String? = null,
    val dialogShouldOpenLogin: Boolean = false,

    // ✅ BottomSheet commentaires
    val isCommentsSheetVisible: Boolean = false,
    val selectedPostIdForComments: String? = null
)