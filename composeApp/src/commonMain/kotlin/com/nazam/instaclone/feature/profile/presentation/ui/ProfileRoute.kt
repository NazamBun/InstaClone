package com.nazam.instaclone.feature.profile.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.clipboard.rememberClipboardManager
import com.nazam.instaclone.core.media.rememberImagePicker
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.presentation.ui.components.ProfileVisitedTopBar
import com.nazam.instaclone.feature.profile.presentation.viewmodel.ProfileUiEvent
import com.nazam.instaclone.feature.profile.presentation.viewmodel.ProfileViewModel
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.action_retry
import instaclone.composeapp.generated.resources.profile_action_soon
import instaclone.composeapp.generated.resources.share_copied
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val DEEPLINK_PROFILE_PREFIX = "instaclone://profile/"
private val VisitedProfileTopPadding = 64.dp

@Composable
fun ProfileRoute(
    contentPadding: PaddingValues,
    onNavigate: (Screen) -> Unit,
    onBackClick: () -> Unit,
    isVisitedProfile: Boolean,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onMoreClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onEditCoverClick: () -> Unit,
    onEditAvatarClick: () -> Unit,
    onPostClick: (VsPost) -> Unit
) {
    val vm: ProfileViewModel = koinViewModel()
    val state by vm.uiState.collectAsState()
    val clipboard = rememberClipboardManager()
    val snackbarHostState = remember { SnackbarHostState() }
    val copiedLabel = stringResource(Res.string.share_copied)
    val soonLabel = stringResource(Res.string.profile_action_soon)
    var pendingSnack by remember { mutableStateOf<String?>(null) }
    val pickAvatar = rememberImagePicker(onImagePicked = vm::onAvatarSelected)

    LaunchedEffect(Unit) {
        vm.events.collectLatest { if (it is ProfileUiEvent.Navigate) onNavigate(it.screen) }
    }

    pendingSnack?.let { text ->
        LaunchedEffect(text) {
            snackbarHostState.showSnackbar(text)
            pendingSnack = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.ui != null -> {
                val ui = state.ui ?: return
                ProfileScreen(
                    ui = ui,
                    contentPadding = contentPadding,
                    onFollowClick = vm::onFollowClicked,
                    onMessageClick = onMessageClick,
                    onMoreClick = onMoreClick,
                    onLogoutClick = vm::logout,
                    onEditProfileClick = onEditProfileClick,
                    onEditCoverClick = onEditCoverClick,
                    onEditAvatarClick = pickAvatar,
                    onPostClick = onPostClick,
                    modifier = Modifier.fillMaxSize().padding(
                        top = if (isVisitedProfile) VisitedProfileTopPadding else 0.dp
                    )
                )
                if (isVisitedProfile) {
                    ProfileVisitedTopBar(
                        ui = ui,
                        onBackClick = onBackClick,
                        onCopyUrlClick = {
                            clipboard.setText("$DEEPLINK_PROFILE_PREFIX${ui.username}")
                            pendingSnack = copiedLabel
                        },
                        onRestrictClick = { pendingSnack = soonLabel },
                        onReportClick = { pendingSnack = soonLabel },
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = vm::load) {
                    Text(stringResource(Res.string.action_retry))
                }
            }
            // Note: l'ancien code affichait state.error?.asString() comme label de bouton
            // (étrange UX). On affiche maintenant un retry standard. L'erreur sera visible
            // via snackbar dans une future itération si nécessaire.
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
