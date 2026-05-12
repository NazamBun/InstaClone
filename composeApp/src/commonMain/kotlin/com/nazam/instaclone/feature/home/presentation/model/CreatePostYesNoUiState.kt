package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.core.ui.UiText

data class CreatePostYesNoUiState(
    val question: String = "",
    val imageLocalUri: String = "",
    val imageUploadedUrl: String = "",
    val isUploadingImage: Boolean = false,
    val imageUploadPercent: Int? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val submitBlockedReason: UiText? = null,
    val authBlocked: Boolean = false
)
