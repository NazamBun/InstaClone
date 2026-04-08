package com.nazam.instaclone.feature.notifications.domain.repository

import com.nazam.instaclone.feature.notifications.domain.model.AppNotification
import kotlinx.coroutines.flow.StateFlow

interface NotificationsRepository {
    fun observeNotifications(): StateFlow<List<AppNotification>>
    fun markAsRead(notificationId: String)
}
