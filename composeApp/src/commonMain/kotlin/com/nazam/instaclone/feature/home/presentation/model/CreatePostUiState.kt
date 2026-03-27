package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.core.ui.UiText

data class CreatePostUiState(
    val question: String = "",
    val leftLabel: String = "",
    val rightLabel: String = "",
    val leftLocalUri: String = "",
    val rightLocalUri: String = "",
    val leftUploadedUrl: String = "",
    val rightUploadedUrl: String = "",
    val selectedVsBadgeId: String = "",
    val isUploadingLeft: Boolean = false,
    val isUploadingRight: Boolean = false,
    val leftUploadPercent: Int? = null,
    val rightUploadPercent: Int? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val isSubmitEnabled: Boolean = false,
    val submitBlockedReason: UiText? = null
)
