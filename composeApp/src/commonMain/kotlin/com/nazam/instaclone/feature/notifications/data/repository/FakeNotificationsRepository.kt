package com.nazam.instaclone.feature.notifications.data.repository

import com.nazam.instaclone.feature.notifications.data.fake.FakeNotifications
import com.nazam.instaclone.feature.notifications.domain.model.AppNotification
import com.nazam.instaclone.feature.notifications.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FakeNotificationsRepository : NotificationsRepository {

    private val notifications = MutableStateFlow(FakeNotifications.items())

    override fun observeNotifications(): StateFlow<List<AppNotification>> {
        return notifications
    }

    override fun markAsRead(notificationId: String) {
        notifications.update { items ->
            items.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
        }
    }
}
