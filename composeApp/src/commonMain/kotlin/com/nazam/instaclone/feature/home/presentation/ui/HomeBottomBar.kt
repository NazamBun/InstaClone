package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    // Couleurs cohérentes avec ton feed
    val background = Color(0xFF050509)
    val accent = Color(0xFF2F5BFF)
    val normal = Color.White.copy(alpha = 0.85f)

    // ExplorePager = Explore (pour que l’icône reste sélectionnée)
    val normalizedSelected =
        if (selectedScreen == Screen.ExplorePager) Screen.Explore else selectedScreen

    @Composable
    fun Item(
        isSelected: Boolean,
        contentDescription: String,
        onClick: () -> Unit,
        icon: @Composable () -> Unit
    ) {
        NavigationBarItem(
            selected = isSelected,
            onClick = onClick,
            icon = { icon() },
            label = null,
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = accent,
                unselectedIconColor = normal,
                indicatorColor = Color.Transparent
            )
        )
    }

    NavigationBar(
        modifier = modifier,
        containerColor = background,
        tonalElevation = 0f
    ) {
        Item(
            isSelected = normalizedSelected == Screen.Home,
            contentDescription = stringResource(Res.string.bottom_home),
            onClick = onHomeClick
        ) {
            Icon(Icons.Filled.Home, contentDescription = null)
        }

        Item(
            isSelected = normalizedSelected == Screen.Explore,
            contentDescription = stringResource(Res.string.bottom_explore),
            onClick = onExploreClick
        ) {
            Icon(Icons.Filled.Search, contentDescription = null)
        }

        // Bouton créer seulement si autorisé
        if (canCreatePost) {
            Item(
                isSelected = normalizedSelected == Screen.CreatePost,
                contentDescription = stringResource(Res.string.bottom_create),
                onClick = onCreatePostClick
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }

        Item(
            isSelected = normalizedSelected == Screen.Notifications,
            contentDescription = stringResource(Res.string.bottom_notifications),
            onClick = onNotificationsClick
        ) {
            Icon(Icons.Filled.Notifications, contentDescription = null)
        }

        val profileDesc = stringResource(
            if (isLoggedIn) Res.string.bottom_profile else Res.string.dialog_login
        )

        Item(
            isSelected = normalizedSelected == Screen.Profile,
            contentDescription = profileDesc,
            onClick = onProfileOrLoginClick
        ) {
            Icon(Icons.Filled.Person, contentDescription = null)
        }
    }
}
