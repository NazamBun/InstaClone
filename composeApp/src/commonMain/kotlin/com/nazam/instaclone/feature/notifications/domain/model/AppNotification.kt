package com.nazam.instaclone.feature.notifications.domain.model

import com.nazam.instaclone.core.navigation.Screen

data class AppNotification(
    val id: String,
    val emoji: String,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean,
    val target: NotificationTarget
)

data class NotificationTarget(
    val screen: Screen,
    val type: NotificationTargetType,
    val postId: String? = null,
    val authorId: String? = null,
    val openCommentsOnOpen: Boolean = false
)

enum class NotificationTargetType {
    HOME_FEED,
    EXPLORE_FEED,
    POST,
    PROFILE
}
