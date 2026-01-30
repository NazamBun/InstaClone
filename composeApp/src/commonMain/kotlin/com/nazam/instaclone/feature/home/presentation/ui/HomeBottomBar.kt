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
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.bottom_create
import instaclone.composeapp.generated.resources.bottom_explore
import instaclone.composeapp.generated.resources.bottom_home
import instaclone.composeapp.generated.resources.bottom_notifications
import instaclone.composeapp.generated.resources.bottom_profile
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

    val normalizedSelectedScreen =
        if (selectedScreen == Screen.ExplorePager) Screen.Explore else selectedScreen

    fun colorFor(screen: Screen): Color =
        if (normalizedSelectedScreen == screen) accent else normal

    fun weightFor(screen: Screen): FontWeight =
        if (normalizedSelectedScreen == screen) FontWeight.Bold else FontWeight.Normal

    @Composable
    fun BottomItem(
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
        BottomItem(
            label = stringResource(Res.string.bottom_home),
            screen = Screen.Home,
            onClick = onHomeClick
        )

        BottomItem(
            label = stringResource(Res.string.bottom_explore),
            screen = Screen.Explore,
            onClick = onExploreClick
        )

        BottomItem(
            label = stringResource(Res.string.bottom_create),
            screen = Screen.CreatePost,
            onClick = onCreatePostClick
        )

        BottomItem(
            label = stringResource(Res.string.bottom_notifications),
            screen = Screen.Notifications,
            onClick = onNotificationsClick
        )

        BottomItem(
            label = stringResource(Res.string.bottom_profile),
            screen = Screen.Profile,
            onClick = onProfileClick
        )
    }
}