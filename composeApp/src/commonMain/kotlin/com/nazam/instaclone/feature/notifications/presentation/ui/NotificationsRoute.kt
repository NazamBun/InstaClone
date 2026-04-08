package com.nazam.instaclone.feature.notifications.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi

@Composable
fun NotificationsRoute(
    contentPadding: PaddingValues,
    items: List<NotificationUi>,
    onNotificationClick: (NotificationUi) -> Unit,
    onScreenShown: () -> Unit
) {
    LaunchedEffect(Unit) {
        onScreenShown()
    }

    NotificationsScreen(
        contentPadding = contentPadding,
        items = items,
        onNotificationClick = onNotificationClick
    )
}
