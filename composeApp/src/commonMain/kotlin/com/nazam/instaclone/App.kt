package com.nazam.instaclone

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.nazam.instaclone.core.di.appModule
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.auth.presentation.ui.LoginScreen
import com.nazam.instaclone.feature.auth.presentation.ui.SignupScreen
import com.nazam.instaclone.feature.home.data.repository.HomeRepositoryImpl
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.presentation.ui.CreatePostRoute
import com.nazam.instaclone.feature.home.presentation.ui.HomeScreen
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel
import org.koin.core.context.startKoin

// Démarre Koin une fois pour tout le monde (repositories, use cases, etc.)
fun initKoin() {
    startKoin {
        modules(appModule)
    }
}

@Composable
fun App() {

    // on démarre Koin (simple pour l’instant)
    initKoin()

    var currentScreen by remember { mutableStateOf(Screen.Login) }

    // 🔹 On crée le ViewModel de création de post une seule fois
    val createPostViewModel = remember {
        // Ici on construit la chaîne "propre" à la main.
        // Plus tard on pourra aussi injecter ce ViewModel avec Koin si on veut.

        val client = SupabaseClientProvider.client
        val homeRepository = HomeRepositoryImpl(client)
        val createPostUseCase = CreatePostUseCase(homeRepository)

        CreatePostViewModel(createPostUseCase)
    }

    MaterialTheme {

        when (currentScreen) {
            Screen.Login -> LoginScreen(
                onNavigateToSignup = { currentScreen = Screen.Signup },
                onNavigateToHome = { currentScreen = Screen.Home }
            )

            Screen.Signup -> SignupScreen(
                onNavigateToLogin = { currentScreen = Screen.Login }
            )

            Screen.Home -> HomeScreen(
                onNavigateToCreatePost = { currentScreen = Screen.CreatePost }
            )

            Screen.CreatePost -> CreatePostRoute(
                viewModel = createPostViewModel,
                onBack = { currentScreen = Screen.Home },
                onPostCreated = { currentScreen = Screen.Home }
            )
        }
    }
}