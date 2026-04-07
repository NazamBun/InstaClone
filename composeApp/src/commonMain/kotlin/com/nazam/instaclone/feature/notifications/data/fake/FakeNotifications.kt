package com.nazam.instaclone.feature.notifications.data.fake

import com.nazam.instaclone.feature.notifications.domain.model.AppNotification
import com.nazam.instaclone.feature.notifications.domain.model.NotificationTarget
import com.nazam.instaclone.feature.notifications.domain.model.NotificationTargetType

object FakeNotifications {

    fun items(): List<AppNotification> {
        return listOf(
            AppNotification(
                id = "notif_comment",
                emoji = "💬",
                title = "Nouveau commentaire",
                body = "Quelqu’un a commenté ton VS.",
                time = "Il y a 12 min",
                isRead = false,
                target = NotificationTarget(
                    type = NotificationTargetType.POST,
                    postId = "post_comment_1",
                    openCommentsOnOpen = true
                )
            ),
            AppNotification(
                id = "notif_follow",
                emoji = "✨",
                title = "Nouveau post suivi",
                body = "Un compte que tu suis a publié un VS.",
                time = "Hier",
                isRead = true,
                target = NotificationTarget(
                    type = NotificationTargetType.PROFILE,
                    authorId = "author_followed_1"
                )
            )
        )
    }
}
