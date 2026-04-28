package com.nazam.instaclone.feature.notifications.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationUi
import com.nazam.instaclone.feature.notifications.presentation.viewmodel.NotificationsUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.notifications_empty
import instaclone.composeapp.generated.resources.notifications_section_recent
import instaclone.composeapp.generated.resources.notifications_title
import org.jetbrains.compose.resources.stringResource

private val BackgroundColor = Color(0xFF050509)
private val ContentHorizontalPadding = 16.dp
private val ContentVerticalPadding = 14.dp
private val ItemSpacing = 10.dp

@Composable
fun NotificationsScreen(
    contentPadding: PaddingValues,
    uiState: NotificationsUiState,
    onNotificationClick: (NotificationUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize().background(BackgroundColor).padding(contentPadding)
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            uiState.error != null -> CenteredText(uiState.error.asString())
            uiState.isEmpty -> CenteredText(stringResource(Res.string.notifications_empty))
            else -> NotificationsList(items = uiState.items, onClick = onNotificationClick)
        }
    }
}

@Composable
private fun BoxScope.CenteredText(text: String) {
    Text(text = text, color = Color.White, modifier = Modifier.align(Alignment.Center))
}

@Composable
private fun NotificationsList(
    items: List<NotificationUi>,
    onClick: (NotificationUi) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(
            horizontal = ContentHorizontalPadding,
            vertical = ContentVerticalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(ItemSpacing)
    ) {
        item {
            Text(
                text = stringResource(Res.string.notifications_title),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }
        item {
            Text(
                text = stringResource(Res.string.notifications_section_recent),
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        items(items, key = { it.id }) { item ->
            NotificationRow(ui = item, onClick = onClick)
        }
    }
}
