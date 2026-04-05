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
import com.nazam.instaclone.feature.home.domain.usecase.GetPostByIdUseCase
import com.nazam.instaclone.feature.home.presentation.ui.CreatePostRoute
import com.nazam.instaclone.feature.home.presentation.ui.HomeBottomBar
import com.nazam.instaclone.feature.home.presentation.ui.HomeRoute
import com.nazam.instaclone.feature.home.presentation.ui.categories.CategoriesRoute
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExplorePagerRoute
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExplorePagerStore
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExploreRoute
import com.nazam.instaclone.feature.home.presentation.ui.notifications.NotificationPostStore
import com.nazam.instaclone.feature.home.presentation.ui.notifications.NotificationTargetType
import com.nazam.instaclone.feature.home.presentation.ui.notifications.NotificationUi
import com.nazam.instaclone.feature.home.presentation.ui.notifications.NotificationsFakeData
import com.nazam.instaclone.feature.home.presentation.ui.notifications.NotificationsScreen
import com.nazam.instaclone.feature.profile.presentation.navigation.ProfileTargetStore
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileRoute
import com.nazam.instaclone.feature.profile.presentation.ui.edit.EditProfileRoute
import org.koin.compose.koinInject

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    val sessionManager: SessionManager = koinInject()
    val getPostByIdUseCase: GetPostByIdUseCase = koinInject()

    val currentUser by sessionManager.user.collectAsState()
    val isLoggedIn = currentUser != null
    val canCreatePost = CreatePostAccess.canCreate(currentUser)

    var notifications by remember { mutableStateOf(NotificationsFakeData.items()) }
    val notificationsCount = notifications.count { it.isNew }

    val snackbarHostState = remember { SnackbarHostState() }
    SnackbarEffect(hostState = snackbarHostState)

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun requireAuth(target: Screen, returnScreen: Screen) {
        NavigationStore.setAuthReturnIfEmpty(returnScreen)
        NavigationStore.setAfterLogin(target)
        navigateTo(Screen.Login)
    }

    fun openMyProfile() {
        ProfileTargetStore.openSelf()
        navigateTo(Screen.Profile)
    }

    fun openNotifications() {
        if (isLoggedIn) navigateTo(Screen.Notifications)
        else requireAuth(Screen.Notifications, currentScreen)
    }

    fun markNotificationRead(item: NotificationUi) {
        notifications = notifications.map { current ->
            if (current.id == item.id) current.copy(isNew = false) else current
        }
    }

    fun openNotificationTarget(item: NotificationUi) {
        markNotificationRead(item)

        when (item.targetType) {
            NotificationTargetType.PROFILE -> {
                val authorId = item.authorId ?: return
                ProfileTargetStore.open(
                    userId = authorId,
                    emailFallback = authorId,
                    returnScreen = Screen.Notifications
                )
                navigateTo(Screen.UserProfile)
            }

            NotificationTargetType.HOME_FEED -> {
                navigateTo(Screen.Home)
            }

            NotificationTargetType.EXPLORE_FEED -> {
                navigateTo(Screen.Explore)
            }

            NotificationTargetType.POST -> {
                val postId = item.postId ?: return
                NotificationPostStore.clear()

                val result = kotlinx.coroutines.runBlocking {
                    getPostByIdUseCase.execute(postId)
                }

                result.onSuccess { post ->
                    NotificationPostStore.open(post)
                    ExplorePagerStore.open(
                        postIds = listOf(post.id),
                        startPostId = post.id
                    )
                    navigateTo(Screen.ExplorePager)
                }.onFailure {
                    navigateTo(Screen.Home)
                }
            }
        }
    }

    fun isProtected(screen: Screen): Boolean {
        return screen == Screen.Profile ||
            screen == Screen.EditProfile ||
            screen == Screen.CreatePost ||
            screen == Screen.Notifications
    }

    LaunchedEffect(Unit) {
        sessionManager.refresh()
    }

    LaunchedEffect(currentScreen, isLoggedIn) {
        if (!isLoggedIn && isProtected(currentScreen)) {
            requireAuth(currentScreen, Screen.Home)
        }
    }

    val shouldShowBottomBar =
        currentScreen != Screen.Login &&
            currentScreen != Screen.Signup &&
            currentScreen != Screen.UserProfile

    MaterialTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
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
                            if (isLoggedIn) navigateTo(Screen.CreatePost)
                            else requireAuth(Screen.CreatePost, currentScreen)
                        },
                        onNotificationsClick = { openNotifications() },
                        onProfileOrLoginClick = {
                            if (isLoggedIn) openMyProfile()
                            else requireAuth(Screen.Profile, currentScreen)
                        }
                    )
                }
            }
        ) { padding: PaddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    Screen.Home -> HomeRoute(onNavigate = ::navigateTo, contentPadding = padding)
                    Screen.Explore -> ExploreRoute(onNavigate = ::navigateTo, contentPadding = padding)
                    Screen.ExplorePager -> ExplorePagerRoute(onNavigate = ::navigateTo, contentPadding = padding)
                    Screen.CreatePost -> CreatePostRoute(onNavigate = ::navigateTo)
                    Screen.Categories -> CategoriesRoute(onNavigate = ::navigateTo)
                    Screen.Login -> LoginRoute(onNavigate = ::navigateTo)
                    Screen.Signup -> SignupRoute(onNavigate = ::navigateTo)

                    Screen.Notifications -> NotificationsScreen(
                        contentPadding = padding,
                        items = notifications,
                        onNotificationClick = ::openNotificationTarget
                    )

                    Screen.Profile -> ProfileRoute(
                        contentPadding = padding,
                        onNavigate = ::navigateTo,
                        onBackClick = {},
                        isVisitedProfile = false,
                        onFollowClick = {},
                        onMessageClick = {},
                        onMoreClick = {},
                        onEditProfileClick = { navigateTo(Screen.EditProfile) },
                        onEditCoverClick = {},
                        onEditAvatarClick = {},
                        onPostClick = { _: VsPost -> }
                    )

                    Screen.UserProfile -> ProfileRoute(
                        contentPadding = padding,
                        onNavigate = ::navigateTo,
                        onBackClick = {
                            navigateTo(ProfileTargetStore.getReturnScreen() ?: Screen.Home)
                        },
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
                        contentPadding = padding,
                        onNavigate = ::navigateTo
                    )
                }
            }
        }
    }
}
