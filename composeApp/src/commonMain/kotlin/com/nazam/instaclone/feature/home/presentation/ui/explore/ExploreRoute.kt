package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import org.koin.compose.koinInject

@Composable
fun ExploreRoute(
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues
) {
    val viewModel: HomeViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    ExploreScreen(
        ui = ui,
        contentPadding = contentPadding,
        onCategoryClick = viewModel::onExploreCategoryClicked,
        onClearCategory = viewModel::onExploreClearCategory,

        // ✅ Maintenant : clic -> ouvre le swipe horizontal dans la catégorie
        onPostClick = { post ->
            ExplorePagerStore.open(
                categoryId = post.category,
                startPostId = post.id
            )
            onNavigate(Screen.ExplorePager)
        }
    )
}