package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.navigation.Screen
import instaclone.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

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

    // ExplorePager est un sous-écran de Explore
    val normalizedSelectedScreen =
        if (selectedScreen == Screen.ExplorePager) Screen.Explore else selectedScreen

    fun colorFor(screen: Screen): Color =
        if (normalizedSelectedScreen == screen) accent else normal

    fun weightFor(screen: Screen): FontWeight =
        if (normalizedSelectedScreen == screen) FontWeight.Bold else FontWeight.Normal

    @Composable
    fun RowScope.BottomItem(
        label: String,
        screen: Screen,
        onClick: () -> Unit
    ) {
        Text(
            text = label,
            color = colorFor(screen),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = weightFor(screen),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick)
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .heightIn(min = 56.dp)
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        BottomItem(stringResource(Res.string.bottom_home), Screen.Home, onHomeClick)
        BottomItem(stringResource(Res.string.bottom_explore), Screen.Explore, onExploreClick)
        BottomItem(stringResource(Res.string.bottom_create), Screen.CreatePost, onCreatePostClick)
        BottomItem(stringResource(Res.string.bottom_notifications), Screen.Notifications, onNotificationsClick)
        BottomItem(stringResource(Res.string.bottom_profile), Screen.Profile, onProfileClick)
    }
}