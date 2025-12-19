package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel

@Composable
fun CreatePostRoute(
    onBack: () -> Unit,
    onPostCreated: () -> Unit
) {
    // ✅ Le ViewModel vit tant que l’écran existe
    val viewModel = remember { CreatePostViewModel() }

    // ✅ Nettoyage propre quand on quitte l’écran
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clear()
        }
    }

    CreatePostScreen(
        viewModel = viewModel,
        onBack = onBack,
        onPostCreated = onPostCreated
    )
}