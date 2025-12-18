package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel

@Composable
fun CreatePostRoute(
    onBack: () -> Unit,
    onPostCreated: () -> Unit
) {
    val viewModel = remember { CreatePostViewModel() }

    CreatePostScreen(
        viewModel = viewModel,
        onBack = onBack,
        onPostCreated = onPostCreated
    )
}