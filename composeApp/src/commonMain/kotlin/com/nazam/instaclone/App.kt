package com.nazam.instaclone

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.nazam.instaclone.core.di.appModule
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.presentation.ui.LoginScreen
import com.nazam.instaclone.feature.auth.presentation.ui.SignupScreen
import com.nazam.instaclone.feature.home.presentation.HomeScreen
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}

@Composable
fun App() {

    initKoin()

    var currentScreen by remember { mutableStateOf(Screen.Login) }

    MaterialTheme {

        when (currentScreen) {
            Screen.Login -> LoginScreen(
                onNavigateToSignup = { currentScreen = Screen.Signup },
                onNavigateToHome = { currentScreen = Screen.Home }
            )

            Screen.Signup -> SignupScreen(
                onNavigateToLogin = { currentScreen = Screen.Login }
            )

            Screen.Home -> HomeScreen()
        }
    }
}