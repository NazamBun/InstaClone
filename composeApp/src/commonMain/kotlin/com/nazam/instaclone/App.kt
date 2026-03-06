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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nazam.instaclone.core.access.CreatePostAccess
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.feature.auth.presentation.ui.LoginRoute
import com.nazam.instaclone.feature.auth.presentation.ui.SignupRoute
import com.nazam.instaclone.feature.home.presentation.ui.CreatePostRoute
import com.nazam.instaclone.feature.home.presentation.ui.HomeBottomBar
import com.nazam.instaclone.feature.home.presentation.ui.HomeRoute
import com.nazam.instaclone.feature.home.presentation.ui.categories.CategoriesRoute
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExplorePagerRoute
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExploreRoute
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileRoute
import com.nazam.instaclone.feature.profile.presentation.ui.edit.EditProfileRoute
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.placeholder_notifications_soon
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    val sessionManager: SessionManager = koinInject()
    val currentUser by sessionManager.user.collectAsState()
    val isLoggedIn = currentUser != null
    val canCreatePost = CreatePostAccess.canCreate(currentUser)

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun isProtected(screen: Screen): Boolean {
        return screen == Screen.Profile ||
            screen == Screen.EditProfile ||
            screen == Screen.CreatePost ||
            screen == Screen.Notifications
    }

    fun requireAuth(target: Screen, returnScreen: Screen) {
        NavigationStore.setAuthReturnIfEmpty(returnScreen)
        NavigationStore.setAfterLogin(target)
        navigateTo(Screen.Login)
    }

    LaunchedEffect(Unit) {
        sessionManager.refresh()
    }

    LaunchedEffect(currentScreen, isLoggedIn) {
        if (!isLoggedIn && isProtected(currentScreen)) {
            requireAuth(target = currentScreen, returnScreen = Screen.Home)
        }
    }

    val shouldShowBottomBar = currentScreen != Screen.Login && currentScreen != Screen.Signup

    MaterialTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            bottomBar = {
                if (shouldShowBottomBar) {
                    HomeBottomBar(
                        selectedScreen = currentScreen,
                        isLoggedIn = isLoggedIn,
                        canCreatePost = canCreatePost,
                        onHomeClick = { navigateTo(Screen.Home) },
                        onExploreClick = { navigateTo(Screen.Explore) },
                        onCreatePostClick = {
                            if (isLoggedIn) navigateTo(Screen.CreatePost)
                            else requireAuth(target = Screen.CreatePost, returnScreen = currentScreen)
                        },
                        onNotificationsClick = {
                            if (isLoggedIn) navigateTo(Screen.Notifications)
                            else requireAuth(target = Screen.Notifications, returnScreen = currentScreen)
                        },
                        onProfileOrLoginClick = {
                            if (isLoggedIn) navigateTo(Screen.Profile)
                            else requireAuth(target = Screen.Profile, returnScreen = currentScreen)
                        }
                    )
                }
            }
        ) { padding: PaddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    Screen.Home -> HomeRoute(
                        onNavigate = ::navigateTo,
                        contentPadding = padding
                    )

                    Screen.Explore -> ExploreRoute(
                        onNavigate = ::navigateTo,
                        contentPadding = padding
                    )

                    Screen.ExplorePager -> ExplorePagerRoute(
                        onNavigate = ::navigateTo,
                        contentPadding = padding
                    )

                    Screen.CreatePost -> CreatePostRoute(onNavigate = ::navigateTo)
                    Screen.Categories -> CategoriesRoute(onNavigate = ::navigateTo)
                    Screen.Login -> LoginRoute(onNavigate = ::navigateTo)
                    Screen.Signup -> SignupRoute(onNavigate = ::navigateTo)

                    Screen.Notifications -> SimplePlaceholder(
                        title = stringResource(Res.string.placeholder_notifications_soon),
                        contentPadding = padding
                    )

                    Screen.Profile -> ProfileRoute(
                        contentPadding = padding,
                        onNavigate = ::navigateTo,
                        onFollowClick = {},
                        onMessageClick = {},
                        onEditProfileClick = { navigateTo(Screen.EditProfile) },
                        onEditCoverClick = {},
                        onEditAvatarClick = {},
                        onPostClick = { _ -> },
                        onMoreClick = {}
                    )

                    Screen.EditProfile -> EditProfileRoute(
                        contentPadding = padding,
                        onNavigate = ::navigateTo
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
