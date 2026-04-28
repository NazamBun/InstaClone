package com.nazam.instaclone.feature.notifications.presentation.badge

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.notifications.domain.usecase.ObserveNotificationsUseCase
import com.nazam.instaclone.feature.notifications.domain.usecase.RefreshNotificationsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Singleton léger qui expose le compteur de notifications non lues
 * pour la BottomBar (badge global).
 *
 * - Vit toute la durée de l'app (single Koin)
 * - Observe en continu pour réagir aux nouvelles notifs
 * - Indépendant de NotificationsViewModel (qui ne vit que sur l'écran Notifications)
 */
class NotificationsBadgeStore(
    dispatchers: AppDispatchers,
    observeNotificationsUseCase: ObserveNotificationsUseCase,
    private val refreshNotificationsUseCase: RefreshNotificationsUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.main)

    /**
     * Compteur des notifs non lues (isRead == false).
     * Exposé en StateFlow pour collectAsState() dans Compose.
     */
    val unreadCount: StateFlow<Int> = observeNotificationsUseCase.execute()
        .map { list -> list.count { !it.isRead } }
        .stateIn(scope = scope, started = SharingStarted.Eagerly, initialValue = 0)

    /**
     * Force un rafraîchissement (utile au démarrage de l'app).
     */
    fun refresh() {
        scope.launch {
            runCatching { refreshNotificationsUseCase.execute() }
        }
    }
}
