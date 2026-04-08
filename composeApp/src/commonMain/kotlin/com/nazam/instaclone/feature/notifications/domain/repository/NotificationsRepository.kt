package com.nazam.instaclone.feature.notifications.domain.repository

import com.nazam.instaclone.feature.notifications.domain.model.AppNotification
import kotlinx.coroutines.flow.StateFlow

interface NotificationsRepository {
    fun observeNotifications(): StateFlow<List<AppNotification>>
    suspend fun refresh()
    suspend fun markAsRead(notificationId: String)
}
