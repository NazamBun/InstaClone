package com.nazam.instaclone.feature.notifications.domain.model

import com.nazam.instaclone.core.navigation.Screen

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
                    screen = Screen.Home,
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
                    screen = Screen.Explore,
                    type = NotificationTargetType.PROFILE,
                    authorId = "author_followed_1"
                )
            )
        )
    }
}
