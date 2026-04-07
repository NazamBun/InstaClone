package com.nazam.instaclone.feature.notifications.presentation.viewmodel

import com.nazam.instaclone.feature.notifications.domain.model.FakeNotifications
import com.nazam.instaclone.feature.notifications.presentation.mapper.NotificationUiMapper
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationAction
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NotificationsViewModel {

    private val _notifications = MutableStateFlow(
        NotificationUiMapper.toUiList(FakeNotifications.items())
    )
    val notifications: StateFlow<List<NotificationUi>> = _notifications

    fun markAsRead(notificationId: String) {
        _notifications.update { items ->
            items.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isNew = false)
                } else {
                    notification
                }
            }
        }
    }

    fun unreadCount(): Int {
        return _notifications.value.count { it.isNew }
    }

    fun getAction(item: NotificationUi): NotificationAction? {
        return when (item.targetType) {
            com.nazam.instaclone.feature.notifications.presentation.model.NotificationTargetType.HOME_FEED -> {
                NotificationAction.OpenHomeFeed
            }

            com.nazam.instaclone.feature.notifications.presentation.model.NotificationTargetType.EXPLORE_FEED -> {
                NotificationAction.OpenExploreFeed
            }

            com.nazam.instaclone.feature.notifications.presentation.model.NotificationTargetType.PROFILE -> {
                val authorId = item.authorId ?: return null
                NotificationAction.OpenProfile(authorId = authorId)
            }

            com.nazam.instaclone.feature.notifications.presentation.model.NotificationTargetType.POST -> {
                val postId = item.postId ?: return null
                NotificationAction.OpenPost(
                    postId = postId,
                    openCommentsOnOpen = item.openCommentsOnOpen
                )
            }
        }
    }
}
