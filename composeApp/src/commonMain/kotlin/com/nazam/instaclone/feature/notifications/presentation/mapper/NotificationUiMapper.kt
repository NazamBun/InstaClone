package com.nazam.instaclone.feature.notifications.presentation.mapper

import com.nazam.instaclone.feature.notifications.domain.model.AppNotification
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationTargetType
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi

object NotificationUiMapper {

    fun toUi(notification: AppNotification): NotificationUi {
        return NotificationUi(
            id = notification.id,
            emoji = notification.emoji,
            title = notification.title,
            body = notification.body,
            time = notification.time,
            isNew = !notification.isRead,
            targetScreen = notification.target.screen,
            targetType = notification.target.type.toUiTargetType(),
            postId = notification.target.postId,
            authorId = notification.target.authorId,
            openCommentsOnOpen = notification.target.openCommentsOnOpen
        )
    }

    fun toUiList(items: List<AppNotification>): List<NotificationUi> {
        return items.map(::toUi)
    }
}

private fun com.nazam.instaclone.feature.notifications.domain.model.NotificationTargetType.toUiTargetType():
    NotificationTargetType {
    return when (this) {
        com.nazam.instaclone.feature.notifications.domain.model.NotificationTargetType.HOME_FEED ->
            NotificationTargetType.HOME_FEED

        com.nazam.instaclone.feature.notifications.domain.model.NotificationTargetType.EXPLORE_FEED ->
            NotificationTargetType.EXPLORE_FEED

        com.nazam.instaclone.feature.notifications.domain.model.NotificationTargetType.POST ->
            NotificationTargetType.POST

        com.nazam.instaclone.feature.notifications.domain.model.NotificationTargetType.PROFILE ->
            NotificationTargetType.PROFILE
    }
}
