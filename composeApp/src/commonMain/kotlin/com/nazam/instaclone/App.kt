package com.nazam.instaclone

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.koin.core.context.startKoin
import com.nazam.instaclone.core.di.appModule
import com.nazam.instaclone.feature.auth.presentation.ui.LoginScreen

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}

@Composable
fun App() {
    // ⚠️ Initialise Koin (DI) une seule fois
    initKoin()

    MaterialTheme {
        // ⭐ Pour l’instant : on affiche juste le LoginScreen
        LoginScreen()
    }
}