package com.nazam.instaclone.feature.home.presentation.ui.notifications

import com.nazam.instaclone.core.navigation.Screen

object NotificationsFakeData {

    fun items(): List<NotificationUi> {
        return listOf(
            NotificationUi(
                id = "notif_buzz",
                emoji = "🔥",
                title = "Ton VS explose",
                body = "Ton duel CR7 vs Mbappé commence à buzzer.",
                time = "Il y a 5 min",
                isNew = true,
                targetScreen = Screen.Home,
                targetType = NotificationTargetType.POST,
                postId = "post_cr7_mbappe_1"
            ),
            NotificationUi(
                id = "notif_comment",
                emoji = "💬",
                title = "Nouveau commentaire",
                body = "Quelqu’un a commenté ton VS.",
                time = "Il y a 12 min",
                isNew = true,
                targetScreen = Screen.Home,
                targetType = NotificationTargetType.POST,
                postId = "post_comment_1",
                openCommentsOnOpen = true
            ),
            NotificationUi(
                id = "notif_votes",
                emoji = "🗳️",
                title = "Nouveaux votes",
                body = "Plusieurs personnes ont voté sur ton VS.",
                time = "Il y a 24 min",
                isNew = true,
                targetScreen = Screen.Home,
                targetType = NotificationTargetType.POST,
                postId = "post_votes_1"
            ),
            NotificationUi(
                id = "notif_share",
                emoji = "📤",
                title = "VS partagé",
                body = "Ton VS a été partagé par un utilisateur.",
                time = "Il y a 1 h",
                isNew = false,
                targetScreen = Screen.Home,
                targetType = NotificationTargetType.POST,
                postId = "post_share_1"
            ),
            NotificationUi(
                id = "notif_tight",
                emoji = "👀",
                title = "Duel serré",
                body = "Ton duel est presque à égalité. Relance-le.",
                time = "Il y a 3 h",
                isNew = false,
                targetScreen = Screen.Home,
                targetType = NotificationTargetType.POST,
                postId = "post_tight_1"
            ),
            NotificationUi(
                id = "notif_followed_post",
                emoji = "✨",
                title = "Nouveau post suivi",
                body = "Un compte que tu suis a publié un nouveau VS.",
                time = "Hier",
                isNew = false,
                targetScreen = Screen.Explore,
                targetType = NotificationTargetType.PROFILE,
                authorId = "author_followed_1"
            )
        )
    }
}
