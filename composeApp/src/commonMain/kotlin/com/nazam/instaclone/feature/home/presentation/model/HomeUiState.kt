package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.domain.model.VsPost

data class HomeUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,

    // ✅ infos user (pour l’avatar dans l’input)
    val currentUserEmail: String? = null,
    val currentUserDisplayName: String? = null,

    val votingPostId: String? = null,
    val posts: List<VsPost> = emptyList(),

    // ✅ Dialog (popup)
    val dialogMessage: String? = null,
    val dialogConfirmLabel: String? = null,
    val dialogSecondaryLabel: String? = null,
    val dialogShouldOpenLogin: Boolean = false,
    val dialogShouldOpenSignup: Boolean = false,

    // ✅ Comments panel
    val isCommentsSheetOpen: Boolean = false,
    val commentsPostId: String? = null,
    val isCommentsLoading: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val newCommentText: String = ""
)