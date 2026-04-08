package com.nazam.instaclone.feature.notifications.presentation.viewmodel

import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi

data class NotificationsUiState(
    val items: List<NotificationUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && items.isEmpty()
}
