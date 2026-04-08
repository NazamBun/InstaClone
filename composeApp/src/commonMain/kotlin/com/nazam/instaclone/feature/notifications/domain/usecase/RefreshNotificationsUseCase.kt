package com.nazam.instaclone.feature.notifications.domain.usecase

import com.nazam.instaclone.feature.notifications.domain.repository.NotificationsRepository

class RefreshNotificationsUseCase(
    private val repository: NotificationsRepository
) {
    suspend fun execute() {
        repository.refresh()
    }
}
