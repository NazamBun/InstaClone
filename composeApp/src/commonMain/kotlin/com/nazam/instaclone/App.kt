package com.nazam.instaclone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
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
import com.nazam.instaclone.feature.home.presentation.ui.HomeRoute
import com.nazam.instaclone.feature.home.presentation.ui.categories.CategoriesRoute

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeRoute(onNavigate = ::navigateTo)
                Screen.Login -> LoginRoute(onNavigate = ::navigateTo)
                Screen.Signup -> SignupRoute(onNavigate = ::navigateTo)
                Screen.CreatePost -> CreatePostRoute(onNavigate = ::navigateTo)
                Screen.Categories -> CategoriesRoute(onNavigate = ::navigateTo)
            }
        }
    }
}