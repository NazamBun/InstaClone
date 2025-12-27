package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel
import org.koin.compose.koinInject

@Composable
fun CreatePostRoute(
    onBack: () -> Unit,
    onPostCreated: () -> Unit
) {
    val viewModel: CreatePostViewModel = koinInject()

    DisposableEffect(viewModel) {
        onDispose(viewModel::clear)
    }

    CreatePostScreen(
        viewModel = viewModel,
        onBack = onBack,
        onPostCreated = onPostCreated
    )
}