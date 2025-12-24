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
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.presentation.ui.LoginScreen
import com.nazam.instaclone.feature.auth.presentation.ui.SignupScreen
import com.nazam.instaclone.feature.home.presentation.ui.CreatePostRoute
import com.nazam.instaclone.feature.home.presentation.ui.HomeScreen

@Composable
fun App() {

    var currentScreen by remember { mutableStateOf(Screen.Home) }

    MaterialTheme {
        // ✅ SUPER IMPORTANT KMP:
        // Applique la "safe area" / "system bars" à toute l'app (Android + iOS)
        Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            when (currentScreen) {

                Screen.Home -> HomeScreen(
                    onNavigateToCreatePost = { currentScreen = Screen.CreatePost },
                    onNavigateToLogin = { currentScreen = Screen.Login },
                    onNavigateToSignup = { currentScreen = Screen.Signup }
                )

                Screen.Login -> LoginScreen(
                    onNavigateToSignup = { currentScreen = Screen.Signup },
                    onNavigateToHome = { currentScreen = Screen.Home }
                )

                Screen.Signup -> SignupScreen(
                    onNavigateToLogin = { currentScreen = Screen.Login }
                )

                Screen.CreatePost -> CreatePostRoute(
                    onBack = { currentScreen = Screen.Home },
                    onPostCreated = { currentScreen = Screen.Home }
                )
            }
        }
    }
}