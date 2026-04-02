package com.nazam.instaclone.feature.home.presentation.ui.notifications

data class NotificationUi(
    val emoji: String,
    val title: String,
    val body: String,
    val time: String,
    val isNew: Boolean
)
