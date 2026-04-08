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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationsViewModel(
    observeNotificationsUseCase: ObserveNotificationsUseCase,
    private val refreshNotificationsUseCase: RefreshNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val notificationsFlow =
        observeNotificationsUseCase.execute()
            .map(NotificationUiMapper::toUiList)

    val uiState: StateFlow<NotificationsUiState> =
        combine(notificationsFlow, isLoading, errorMessage) { items, loading, error ->
            NotificationsUiState(
                items = items,
                isLoading = loading,
                errorMessage = error
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = NotificationsUiState(isLoading = true)
        )

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            isLoading.value = true
            errorMessage.value = null
            runCatching {
                refreshNotificationsUseCase.execute()
            }.onFailure {
                errorMessage.value = "Impossible de charger les notifications"
            }
            isLoading.value = false
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
