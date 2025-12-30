package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.koin.compose.koinInject
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel

@Composable
fun CreatePostRoute(
    onBack: () -> Unit,
    onPostCreated: () -> Unit
) {
    // ✅ VM fourni par Koin
    val viewModel: CreatePostViewModel = koinInject()

    // ✅ Nettoyage quand on quitte l’écran
    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    CreatePostScreen(
        viewModel = viewModel,
        onBack = onBack,
        onPostCreated = onPostCreated
    )
}