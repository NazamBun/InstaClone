package com.nazam.instaclone.feature.profile.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.presentation.viewmodel.ProfileUiEvent
import com.nazam.instaclone.feature.profile.presentation.viewmodel.ProfileViewModel
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.action_retry
import instaclone.composeapp.generated.resources.profile_loading
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ProfileRoute(
    contentPadding: PaddingValues,
    onNavigate: (Screen) -> Unit,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onMoreClick: () -> Unit,
    onEditCoverClick: () -> Unit,
    onEditAvatarClick: () -> Unit,
    onPostClick: (VsPost) -> Unit
) {
    val vm: ProfileViewModel = koinInject()
    val state by vm.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { vm.clear() }
    }

    LaunchedEffect(Unit) {
        vm.events.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.Navigate -> onNavigate(event.screen)
            }
        }
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(Res.string.profile_loading),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        state.ui != null -> {
            val ui = state.ui ?: return
            ProfileScreen(
                ui = ui,
                contentPadding = contentPadding,
                onFollowClick = onFollowClick,
                onMessageClick = onMessageClick,
                onMoreClick = onMoreClick,
                onLogoutClick = vm::logout,
                onEditCoverClick = onEditCoverClick,
                onEditAvatarClick = onEditAvatarClick,
                onPostClick = onPostClick
            )
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.error?.asString() ?: "")
                    Button(
                        onClick = vm::load,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(stringResource(Res.string.action_retry))
                    }
                }
            }
        }
    }
}
