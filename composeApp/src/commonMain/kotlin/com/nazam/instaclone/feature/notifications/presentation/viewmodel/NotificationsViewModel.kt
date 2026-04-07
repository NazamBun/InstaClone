package com.nazam.instaclone.feature.notifications.presentation.viewmodel

import com.nazam.instaclone.feature.notifications.data.fake.FakeNotifications
import com.nazam.instaclone.feature.notifications.presentation.mapper.NotificationUiMapper
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationAction
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationTargetType
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

    fun getAction(item: NotificationUi): NotificationAction? {
        return when (item.targetType) {
            NotificationTargetType.HOME_FEED -> NotificationAction.OpenHomeFeed
            NotificationTargetType.EXPLORE_FEED -> NotificationAction.OpenExploreFeed
            NotificationTargetType.PROFILE -> {
                val authorId = item.authorId ?: return null
                NotificationAction.OpenProfile(authorId = authorId)
            }
            NotificationTargetType.POST -> {
                val postId = item.postId ?: return null
                NotificationAction.OpenPost(
                    postId = postId,
                    openCommentsOnOpen = item.openCommentsOnOpen
                )
            }
        }
    }
}
