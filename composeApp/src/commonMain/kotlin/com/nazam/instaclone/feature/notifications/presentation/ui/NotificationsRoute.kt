package com.nazam.instaclone.feature.notifications.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi

@Composable
fun NotificationsRoute(
    contentPadding: PaddingValues,
    items: List<NotificationUi>,
    onNotificationClick: (NotificationUi) -> Unit
) {
    NotificationsScreen(
        contentPadding = contentPadding,
        items = items,
        onNotificationClick = onNotificationClick
    )
}
