package com.nazam.instaclone.feature.profile.presentation.ui.edit

import com.nazam.instaclone.core.ui.UiText

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val displayName: String = "",
    val username: String = "",
    val bio: String = "",
    val location: String = "",
    val website: String = "",
    val error: UiText? = null
)
