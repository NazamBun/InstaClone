package com.nazam.instaclone.feature.notifications.presentation.viewmodel

import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi

data class NotificationsUiState(
    val items: List<NotificationUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && error == null && items.isEmpty()
}
