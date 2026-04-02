package com.nazam.instaclone.feature.home.presentation.ui.notifications

object NotificationsFakeData {

    fun items(): List<NotificationUi> {
        return listOf(
            NotificationUi(
                emoji = "🔥",
                title = "Ton VS explose",
                body = "Ton duel CR7 vs Mbappé commence à buzzer.",
                time = "Il y a 5 min",
                isNew = true
            ),
            NotificationUi(
                emoji = "💬",
                title = "Nouveau commentaire",
                body = "Quelqu’un a commenté ton VS.",
                time = "Il y a 12 min",
                isNew = true
            ),
            NotificationUi(
                emoji = "🗳️",
                title = "Nouveaux votes",
                body = "Plusieurs personnes ont voté sur ton VS.",
                time = "Il y a 24 min",
                isNew = true
            ),
            NotificationUi(
                emoji = "📤",
                title = "VS partagé",
                body = "Ton VS a été partagé par un utilisateur.",
                time = "Il y a 1 h",
                isNew = false
            ),
            NotificationUi(
                emoji = "👀",
                title = "Duel serré",
                body = "Ton duel est presque à égalité. Relance-le.",
                time = "Il y a 3 h",
                isNew = false
            ),
            NotificationUi(
                emoji = "✨",
                title = "Nouveau post suivi",
                body = "Un compte que tu suis a publié un nouveau VS.",
                time = "Hier",
                isNew = false
            )
        )
    }

    fun unreadCount(): Int {
        return items().count { it.isNew }
    }
}
