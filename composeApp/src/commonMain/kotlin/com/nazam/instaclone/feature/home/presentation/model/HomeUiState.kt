package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.domain.model.VsPost

data class HomeUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val votingPostId: String? = null,
    val posts: List<VsPost> = emptyList(),

    // ✅ Dialog (popup)
    val dialogMessage: String? = null,
    val dialogConfirmLabel: String? = null,      // ex: "Se connecter"
    val dialogSecondaryLabel: String? = null,    // ex: "Créer un compte"
    val dialogShouldOpenLogin: Boolean = false,
    val dialogShouldOpenSignup: Boolean = false,

    // ✅ BottomSheet Comments
    val isCommentsSheetOpen: Boolean = false,
    val commentsPostId: String? = null,
    val isCommentsLoading: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val newCommentText: String = ""
)