package com.nazam.instaclone.feature.notifications.domain.usecase

import com.nazam.instaclone.feature.notifications.domain.model.AppNotification
import com.nazam.instaclone.feature.notifications.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveNotificationsUseCase(
    private val repository: NotificationsRepository
) {
    fun execute(): StateFlow<List<AppNotification>> {
        return repository.observeNotifications()
    }
}
