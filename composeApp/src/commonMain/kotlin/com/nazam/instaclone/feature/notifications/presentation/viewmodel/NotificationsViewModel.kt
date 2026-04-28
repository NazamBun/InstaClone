package com.nazam.instaclone.feature.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.notifications.domain.usecase.MarkNotificationReadUseCase
import com.nazam.instaclone.feature.notifications.domain.usecase.ObserveNotificationsUseCase
import com.nazam.instaclone.feature.notifications.domain.usecase.RefreshNotificationsUseCase
import com.nazam.instaclone.feature.notifications.presentation.mapper.NotificationUiMapper
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationAction
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationTargetType
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.notifications_load_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de l'écran Notifications.
 * Ne vit que pendant l'affichage de l'écran (cycle ViewModel AndroidX).
 * Pour le badge global, voir NotificationsBadgeStore.
 */
class NotificationsViewModel(
    observeNotificationsUseCase: ObserveNotificationsUseCase,
    private val refreshNotificationsUseCase: RefreshNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<UiText?>(null)

    private val notificationsFlow = observeNotificationsUseCase.execute()
        .map(NotificationUiMapper::toUiList)

    val uiState: StateFlow<NotificationsUiState> =
        combine(notificationsFlow, isLoading, errorMessage) { items, loading, error ->
            NotificationsUiState(items = items, isLoading = loading, error = error)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = NotificationsUiState(isLoading = true)
        )

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            runCatching { refreshNotificationsUseCase.execute() }
                .onFailure { errorMessage.value = UiText.Resource(Res.string.notifications_load_error) }
            isLoading.value = false
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            markNotificationReadUseCase.execute(notificationId)
        }
    }

    fun getAction(item: NotificationUi): NotificationAction? = when (item.targetType) {
        NotificationTargetType.HOME_FEED -> NotificationAction.OpenHomeFeed
        NotificationTargetType.EXPLORE_FEED -> NotificationAction.OpenExploreFeed
        NotificationTargetType.PROFILE -> item.authorId?.let(NotificationAction::OpenProfile)
        NotificationTargetType.POST -> item.postId?.let { id ->
            NotificationAction.OpenPost(id, item.openCommentsOnOpen)
        }
    }
}
