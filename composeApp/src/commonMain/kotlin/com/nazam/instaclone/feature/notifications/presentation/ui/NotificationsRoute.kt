package com.nazam.instaclone.feature.notifications.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.notifications.presentation.handler.NotificationsActionHandler
import com.nazam.instaclone.feature.notifications.presentation.viewmodel.NotificationsViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationsRoute(
    contentPadding: PaddingValues,
    onNavigate: (Screen) -> Unit
) {
    val viewModel: NotificationsViewModel = koinViewModel()
    val handler: NotificationsActionHandler = koinInject()
    val ui by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    NotificationsScreen(
        contentPadding = contentPadding,
        uiState = ui,
        onNotificationClick = { item ->
            viewModel.markAsRead(item.id)
            val action = viewModel.getAction(item) ?: return@NotificationsScreen
            scope.launch {
                handler.handle(action = action, onNavigate = onNavigate)
            }
        }
    )
}
