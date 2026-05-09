package com.nazam.instaclone.feature.admin.presentation.model

import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.admin.domain.model.AdminUser

data class AdminUiState(
    val query: String = "",
    val users: List<AdminUser> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val snackbarMessage: UiText? = null,
    val togglingUserId: String? = null,
)
