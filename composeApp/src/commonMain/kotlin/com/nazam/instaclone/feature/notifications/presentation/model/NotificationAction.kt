package com.nazam.instaclone.feature.notifications.presentation.model

sealed interface NotificationAction {
    data object OpenHomeFeed : NotificationAction
    data object OpenExploreFeed : NotificationAction

    data class OpenProfile(
        val authorId: String
    ) : NotificationAction

    data class OpenPost(
        val postId: String,
        val openCommentsOnOpen: Boolean
    ) : NotificationAction
}
