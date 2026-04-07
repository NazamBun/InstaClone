package com.nazam.instaclone.feature.notifications.presentation.model

data class NotificationUi(
    val id: String,
    val emoji: String,
    val title: String,
    val body: String,
    val time: String,
    val isNew: Boolean,
    val targetType: NotificationTargetType,
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
