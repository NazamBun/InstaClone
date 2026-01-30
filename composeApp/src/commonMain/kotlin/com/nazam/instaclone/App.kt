package com.nazam.instaclone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.presentation.ui.LoginRoute
import com.nazam.instaclone.feature.auth.presentation.ui.SignupRoute
import com.nazam.instaclone.feature.home.presentation.ui.CreatePostRoute
import com.nazam.instaclone.feature.home.presentation.ui.HomeBottomBar
import com.nazam.instaclone.feature.home.presentation.ui.HomeRoute
import com.nazam.instaclone.feature.home.presentation.ui.categories.CategoriesRoute
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExplorePagerRoute
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExploreRoute
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.placeholder_notifications_soon
import instaclone.composeapp.generated.resources.placeholder_profile_soon
import org.jetbrains.compose.resources.stringResource

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    MaterialTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            bottomBar = {
                HomeBottomBar(
                    selectedScreen = currentScreen,
                    onHomeClick = { navigateTo(Screen.Home) },
                    onExploreClick = { navigateTo(Screen.Explore) },
                    onCreatePostClick = { navigateTo(Screen.CreatePost) },
                    onNotificationsClick = { navigateTo(Screen.Notifications) },
                    onProfileClick = { navigateTo(Screen.Profile) }
                )
            }
        ) { padding: PaddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    Screen.Home -> HomeRoute(onNavigate = ::navigateTo, contentPadding = padding)
                    Screen.Explore -> ExploreRoute(onNavigate = ::navigateTo, contentPadding = padding)

                    // Swipe horizontal
                    Screen.ExplorePager -> ExplorePagerRoute(onNavigate = ::navigateTo, contentPadding = padding)

                    Screen.CreatePost -> CreatePostRoute(onNavigate = ::navigateTo)
                    Screen.Categories -> CategoriesRoute(onNavigate = ::navigateTo)

                    Screen.Login -> LoginRoute(onNavigate = ::navigateTo)
                    Screen.Signup -> SignupRoute(onNavigate = ::navigateTo)

                    Screen.Notifications -> SimplePlaceholder(
                        title = stringResource(Res.string.placeholder_notifications_soon),
                        contentPadding = padding
                    )

                    Screen.Profile -> SimplePlaceholder(
                        title = stringResource(Res.string.placeholder_profile_soon),
                        contentPadding = padding
                    )
                }
            }
        }
    }
}

@Composable
private fun SimplePlaceholder(
    title: String,
    contentPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        androidx.compose.material3.Text(text = title)
    }
}