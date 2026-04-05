package com.nazam.instaclone.feature.home.presentation.ui.notifications

import com.nazam.instaclone.core.navigation.Screen

data class NotificationUi(
    val id: String,
    val emoji: String,
    val title: String,
    val body: String,
    val time: String,
    val isNew: Boolean,
    val targetScreen: Screen
)
