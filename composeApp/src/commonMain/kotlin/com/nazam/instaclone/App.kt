package com.nazam.instaclone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.nazam.instaclone.core.ui.SnackbarEffect
import com.nazam.instaclone.feature.auth.presentation.ui.LoginRoute
import com.nazam.instaclone.feature.auth.presentation.ui.SignupRoute
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.CreatePostRoute
import com.nazam.instaclone.feature.home.presentation.ui.HomeBottomBar
import com.nazam.instaclone.feature.home.presentation.ui.HomeRoute
import com.nazam.instaclone.feature.home.presentation.ui.categories.CategoriesRoute
import com.nazam.instaclone.feature.home.presentation.ui.createposttype.CreatePostTypeRoute
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExplorePagerRoute
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExploreRoute
import com.nazam.instaclone.feature.notifications.presentation.badge.NotificationsBadgeStore
import com.nazam.instaclone.feature.notifications.presentation.ui.NotificationsRoute
import com.nazam.instaclone.feature.profile.presentation.navigation.ProfileTargetStore
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileRoute
import com.nazam.instaclone.feature.profile.presentation.ui.edit.EditProfileRoute
import org.koin.compose.koinInject

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    val sessionManager: SessionManager = koinInject()
    val notificationsBadgeStore: NotificationsBadgeStore = koinInject()

    val currentUser by sessionManager.user.collectAsState()
    val notificationsCount by notificationsBadgeStore.unreadCount.collectAsState()

    val isLoggedIn = currentUser != null
    val canCreatePost = CreatePostAccess.canCreate(currentUser)

    val snackbarHostState = remember { SnackbarHostState() }
    SnackbarEffect(hostState = snackbarHostState)

    fun navigateTo(screen: Screen) { currentScreen = screen }

    fun requireAuth(target: Screen, returnScreen: Screen) {
        NavigationStore.setAuthReturnIfEmpty(returnScreen)
        NavigationStore.setAfterLogin(target)
        navigateTo(Screen.Login)
    }

    fun openMyProfile() {
        ProfileTargetStore.openSelf()
        navigateTo(Screen.Profile)
    }

    fun isProtected(screen: Screen): Boolean = screen in setOf(
        Screen.Profile, Screen.EditProfile, Screen.CreatePostType,
        Screen.CreatePost, Screen.Notifications
    )

    LaunchedEffect(Unit) {
        sessionManager.refresh()
        notificationsBadgeStore.refresh()
    }

    LaunchedEffect(currentScreen, isLoggedIn) {
        if (!isLoggedIn && isProtected(currentScreen)) {
            requireAuth(currentScreen, Screen.Home)
        }
    }

    val shouldShowBottomBar = currentScreen !in setOf(
        Screen.Login, Screen.Signup, Screen.UserProfile
    )

    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (shouldShowBottomBar) {
                    HomeBottomBar(
                        selectedScreen = currentScreen,
                        isLoggedIn = isLoggedIn,
                        canCreatePost = canCreatePost,
                        notificationsCount = notificationsCount,
                        onHomeClick = { navigateTo(Screen.Home) },
                        onExploreClick = { navigateTo(Screen.Explore) },
                        onCreatePostClick = {
                            if (isLoggedIn) navigateTo(Screen.CreatePostType)
                            else requireAuth(Screen.CreatePostType, currentScreen)
                        },
                        onNotificationsClick = {
                            if (isLoggedIn) navigateTo(Screen.Notifications)
                            else requireAuth(Screen.Notifications, currentScreen)
                        },
                        onProfileOrLoginClick = {
                            if (isLoggedIn) openMyProfile()
                            else requireAuth(Screen.Profile, currentScreen)
                        }
                    )
                }
            }
        ) { padding: PaddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                AppNavHost(
                    currentScreen = currentScreen,
                    contentPadding = padding,
                    onNavigate = ::navigateTo
                )
            }
        }
    }
}

/**
 * Aiguille la navigation simple.
 * Sera remplacé par Voyager / Navigation 3 plus tard.
 */
@Composable
private fun AppNavHost(
    currentScreen: Screen,
    contentPadding: PaddingValues,
    onNavigate: (Screen) -> Unit
) {
    when (currentScreen) {
        Screen.Home -> HomeRoute(onNavigate = onNavigate, contentPadding = contentPadding)
        Screen.Explore -> ExploreRoute(onNavigate = onNavigate, contentPadding = contentPadding)
        Screen.ExplorePager -> ExplorePagerRoute(onNavigate = onNavigate, contentPadding = contentPadding)
        Screen.CreatePostType -> CreatePostTypeRoute(onNavigate = onNavigate)
        Screen.CreatePost -> CreatePostRoute(onNavigate = onNavigate)
        Screen.Categories -> CategoriesRoute(onNavigate = onNavigate)
        Screen.Login -> LoginRoute(onNavigate = onNavigate)
        Screen.Signup -> SignupRoute(onNavigate = onNavigate)
        Screen.Notifications -> NotificationsRoute(
            contentPadding = contentPadding,
            onNavigate = onNavigate
        )
        Screen.Profile -> ProfileRoute(
            contentPadding = contentPadding,
            onNavigate = onNavigate,
            onBackClick = {},
            isVisitedProfile = false,
            onFollowClick = {},
            onMessageClick = {},
            onMoreClick = {},
            onEditProfileClick = { onNavigate(Screen.EditProfile) },
            onEditCoverClick = {},
            onEditAvatarClick = {},
            onPostClick = { _: VsPost -> }
        )
        Screen.UserProfile -> ProfileRoute(
            contentPadding = contentPadding,
            onNavigate = onNavigate,
            onBackClick = { onNavigate(ProfileTargetStore.getReturnScreen() ?: Screen.Home) },
            isVisitedProfile = true,
            onFollowClick = {},
            onMessageClick = {},
            onMoreClick = {},
            onEditProfileClick = {},
            onEditCoverClick = {},
            onEditAvatarClick = {},
            onPostClick = { _: VsPost -> }
        )
        Screen.EditProfile -> EditProfileRoute(
            contentPadding = contentPadding,
            onNavigate = onNavigate
        )
    }
}
