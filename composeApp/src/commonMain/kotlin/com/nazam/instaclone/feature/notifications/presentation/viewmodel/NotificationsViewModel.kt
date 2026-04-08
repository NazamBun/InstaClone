package com.nazam.instaclone.feature.notifications.presentation.viewmodel

import com.nazam.instaclone.feature.notifications.domain.usecase.MarkNotificationReadUseCase
import com.nazam.instaclone.feature.notifications.domain.usecase.ObserveNotificationsUseCase
import com.nazam.instaclone.feature.notifications.domain.usecase.RefreshNotificationsUseCase
import com.nazam.instaclone.feature.notifications.presentation.mapper.NotificationUiMapper
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationAction
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationTargetType
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationsViewModel(
    observeNotificationsUseCase: ObserveNotificationsUseCase,
    private val refreshNotificationsUseCase: RefreshNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val notifications: StateFlow<List<NotificationUi>> =
        observeNotificationsUseCase.execute()
            .map(NotificationUiMapper::toUiList)
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            refreshNotificationsUseCase.execute()
        }
    }

    fun markAsRead(notificationId: String) {
        scope.launch {
            markNotificationReadUseCase.execute(notificationId)
        }
    }

    fun getAction(item: NotificationUi): NotificationAction? {
        return when (item.targetType) {
            NotificationTargetType.HOME_FEED -> NotificationAction.OpenHomeFeed
            NotificationTargetType.EXPLORE_FEED -> NotificationAction.OpenExploreFeed
            NotificationTargetType.PROFILE -> {
                val authorId = item.authorId ?: return null
                NotificationAction.OpenProfile(authorId)
            }
            NotificationTargetType.POST -> {
                val postId = item.postId ?: return null
                NotificationAction.OpenPost(postId, item.openCommentsOnOpen)
            }
        }
    }
}
