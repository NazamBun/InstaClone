package com.nazam.instaclone.feature.notifications.domain.usecase

import com.nazam.instaclone.feature.notifications.domain.repository.NotificationsRepository

class MarkNotificationReadUseCase(
    private val repository: NotificationsRepository
) {
    fun execute(notificationId: String) {
        repository.markAsRead(notificationId)
    }
}
