package com.nazam.instaclone.feature.notifications.presentation.handler

import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.home.domain.usecase.GetPostByIdUseCase
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExplorePagerStore
import com.nazam.instaclone.feature.notifications.presentation.model.NotificationAction
import com.nazam.instaclone.feature.profile.presentation.navigation.ProfileTargetStore

class NotificationsActionHandler(
    private val getPostByIdUseCase: GetPostByIdUseCase
) {
    suspend fun handle(
        action: NotificationAction,
        onNavigate: (Screen) -> Unit
    ) {
        when (action) {
            NotificationAction.OpenHomeFeed -> {
                onNavigate(Screen.Home)
            }

            NotificationAction.OpenExploreFeed -> {
                onNavigate(Screen.Explore)
            }

            is NotificationAction.OpenProfile -> {
                ProfileTargetStore.open(
                    userId = action.authorId,
                    emailFallback = action.authorId,
                    returnScreen = Screen.Notifications
                )
                onNavigate(Screen.UserProfile)
            }

            is NotificationAction.OpenPost -> {
                getPostByIdUseCase.execute(action.postId)
                    .onSuccess { post ->
                        ExplorePagerStore.open(
                            postIds = listOf(post.id),
                            startPostId = post.id,
                            fallbackPosts = listOf(post),
                            pendingCommentsPostId = post.id.takeIf { action.openCommentsOnOpen }
                        )
                        onNavigate(Screen.ExplorePager)
                    }
                    .onFailure {
                        onNavigate(Screen.Home)
                    }
            }
        }
    }
}
