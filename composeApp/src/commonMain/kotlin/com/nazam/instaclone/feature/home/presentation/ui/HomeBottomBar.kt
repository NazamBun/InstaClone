package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.navigation.Screen
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.bottom_create
import instaclone.composeapp.generated.resources.bottom_explore
import instaclone.composeapp.generated.resources.bottom_home
import instaclone.composeapp.generated.resources.bottom_notifications
import instaclone.composeapp.generated.resources.bottom_profile
import instaclone.composeapp.generated.resources.dialog_login
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeBottomBar(
    selectedScreen: Screen,
    isLoggedIn: Boolean,
    canCreatePost: Boolean,
    onHomeClick: () -> Unit,
    onExploreClick: () -> Unit,
    onCreatePostClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileOrLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = Color(0xFF050509)
    val accent = Color(0xFF2F5BFF)
    val normal = Color.White.copy(alpha = 0.85f)

    val normalizedSelected =
        if (selectedScreen == Screen.ExplorePager) Screen.Explore else selectedScreen

    fun tintFor(screen: Screen): Color =
        if (normalizedSelected == screen) accent else normal

    @Composable
    fun RowScope.IconItem(
        screen: Screen,
        contentDescription: String,
        onClick: () -> Unit,
        icon: @Composable () -> Unit
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp)
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }

    @Composable
    fun RowScope.CenterCreateButton(onClick: () -> Unit) {
        // ✅ Gros + central (visible seulement si canCreatePost == true)
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accent, CircleShape)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }

    val profileDesc = stringResource(
        if (isLoggedIn) Res.string.bottom_profile else Res.string.dialog_login
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .heightIn(min = 56.dp)
            .padding(horizontal = 8.dp)
    ) {
        IconItem(
            screen = Screen.Home,
            contentDescription = stringResource(Res.string.bottom_home),
            onClick = onHomeClick
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = null,
                tint = tintFor(Screen.Home)
            )
        }

        IconItem(
            screen = Screen.Explore,
            contentDescription = stringResource(Res.string.bottom_explore),
            onClick = onExploreClick
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = tintFor(Screen.Explore)
            )
        }

        if (canCreatePost) {
            CenterCreateButton(onClick = onCreatePostClick)
        }

        IconItem(
            screen = Screen.Notifications,
            contentDescription = stringResource(Res.string.bottom_notifications),
            onClick = onNotificationsClick
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = tintFor(Screen.Notifications)
            )
        }

        IconItem(
            screen = Screen.Profile,
            contentDescription = profileDesc,
            onClick = onProfileOrLoginClick
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = tintFor(Screen.Profile)
            )
        }
    }
}
