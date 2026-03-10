package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import com.nazam.instaclone.feature.profile.presentation.navigation.ProfileTargetStore
import org.koin.compose.koinInject

@Composable
fun ExplorePagerRoute(
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues
) {
    val viewModel: HomeViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    ExplorePagerScreen(
        ui = ui,
        contentPadding = contentPadding,
        onBackClick = {
            ExplorePagerStore.clear()
            onNavigate(Screen.Explore)
        },
        onVoteLeft = viewModel::voteLeft,
        onVoteRight = viewModel::voteRight,
        onOpenComments = viewModel::openComments,
        onOpenAuthor = { post ->
            val authorId = post.authorId ?: return@ExplorePagerScreen
            ProfileTargetStore.open(
                userId = authorId,
                emailFallback = post.authorName,
                returnScreen = Screen.ExplorePager
            )
            onNavigate(Screen.UserProfile)
        }
    )
}
