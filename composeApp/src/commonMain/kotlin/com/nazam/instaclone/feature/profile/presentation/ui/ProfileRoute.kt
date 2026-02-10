package com.nazam.instaclone.feature.profile.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.presentation.viewmodel.ProfileViewModel
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_loading
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ProfileRoute(
    contentPadding: PaddingValues,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onMoreClick: () -> Unit,
    onEditCoverClick: () -> Unit,
    onEditAvatarClick: () -> Unit,
    onPostClick: (VsPost) -> Unit
) {
    val vm: ProfileViewModel = koinInject()
    val state by vm.uiState.collectAsState()

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(Res.string.profile_loading),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        state.ui != null -> {
            ProfileScreen(
                ui = state.ui!!,
                contentPadding = contentPadding,
                onFollowClick = onFollowClick,
                onMessageClick = onMessageClick,
                onMoreClick = onMoreClick,
                onEditCoverClick = onEditCoverClick,
                onEditAvatarClick = onEditAvatarClick,
                onPostClick = onPostClick
            )
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.error?.asString() ?: "")
            }
        }
    }
}
