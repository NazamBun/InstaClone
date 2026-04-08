package com.nazam.instaclone.feature.notifications.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi
import com.nazam.instaclone.feature.notifications.presentation.viewmodel.NotificationsUiState

@Composable
fun NotificationsRoute(
    contentPadding: PaddingValues,
    uiState: NotificationsUiState,
    onNotificationClick: (NotificationUi) -> Unit,
    onScreenShown: () -> Unit
) {
    LaunchedEffect(Unit) {
        onScreenShown()
    }

    NotificationsScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        onNotificationClick = onNotificationClick
    )
}
