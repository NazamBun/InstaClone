package com.nazam.instaclone.feature.home.presentation.ui.createposttype

import androidx.compose.runtime.Composable
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.home.presentation.model.CreatePostType
import com.nazam.instaclone.feature.home.presentation.type.CreatePostTypeStore

@Composable
fun CreatePostTypeRoute(
    onNavigate: (Screen) -> Unit
) {
    CreatePostTypeScreen(
        onBackClick = { onNavigate(Screen.Home) },
        onTypeSelected = { type: CreatePostType ->
            CreatePostTypeStore.set(type)
            when (type) {
                CreatePostType.DUEL -> onNavigate(Screen.CreatePost)
                CreatePostType.YES_NO -> onNavigate(Screen.CreatePostYesNo)
            }
        }
    )
}
