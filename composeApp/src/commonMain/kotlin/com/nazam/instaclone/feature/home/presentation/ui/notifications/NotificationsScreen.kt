package com.nazam.instaclone.feature.home.presentation.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.notifications_section_recent
import instaclone.composeapp.generated.resources.notifications_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun NotificationsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val items = remember {
        listOf(
            NotificationUi("🔥", "Ton VS explose", "Ton duel CR7 vs Mbappé commence à buzzer.", "Il y a 5 min", true),
            NotificationUi("💬", "Nouveau commentaire", "Quelqu’un a commenté ton VS.", "Il y a 12 min", true),
            NotificationUi("🗳️", "Nouveaux votes", "Plusieurs personnes ont voté sur ton VS.", "Il y a 24 min", true),
            NotificationUi("📤", "VS partagé", "Ton VS a été partagé par un utilisateur.", "Il y a 1 h", false),
            NotificationUi("👀", "Duel serré", "Ton duel est presque à égalité. Relance-le.", "Il y a 3 h", false),
            NotificationUi("✨", "Nouveau post suivi", "Un compte que tu suis a publié un nouveau VS.", "Hier", false)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050509))
            .padding(contentPadding)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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

            items(items) { item ->
                NotificationRow(ui = item)
            }
        }
    }
}
