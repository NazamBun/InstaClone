package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.navigation.Screen

/**
 * Bottom bar (KMP friendly) :
 * - Accueil
 * - Explorer
 * - Créer
 * - Notifications
 * - Profil
 *
 * ✅ Pas d'icônes
 * ✅ Surbrillance = selectedScreen
 * ✅ Fond noir partout (même rendu sur tous les écrans)
 */
@Composable
fun HomeBottomBar(
    selectedScreen: Screen,
    onHomeClick: () -> Unit,
    onExploreClick: () -> Unit,
    onCreatePostClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = Color(0xFF050509)
    val accent = Color(0xFFFF4EB8)
    val normal = Color.White

    fun colorFor(screen: Screen): Color = if (selectedScreen == screen) accent else normal
    fun weightFor(screen: Screen): FontWeight =
        if (selectedScreen == screen) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background) // ✅ fond noir sur tous les écrans
            .heightIn(min = 56.dp)
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Accueil",
            color = colorFor(Screen.Home),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = weightFor(Screen.Home),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onHomeClick() }
        )

        Text(
            text = "Explorer",
            color = colorFor(Screen.Explore),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = weightFor(Screen.Explore),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onExploreClick() }
        )

        Text(
            text = "Créer",
            color = colorFor(Screen.CreatePost),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = weightFor(Screen.CreatePost),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onCreatePostClick() }
        )

        Text(
            text = "Notifs",
            color = colorFor(Screen.Notifications),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = weightFor(Screen.Notifications),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onNotificationsClick() }
        )

        Text(
            text = "Profil",
            color = colorFor(Screen.Profile),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = weightFor(Screen.Profile),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onProfileClick() }
        )
    }
}