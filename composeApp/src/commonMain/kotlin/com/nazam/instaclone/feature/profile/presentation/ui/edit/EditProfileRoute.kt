package com.nazam.instaclone.feature.profile.presentation.ui.edit

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun EditProfileRoute(
    contentPadding: PaddingValues,
    onNavigate: (Screen) -> Unit
) {
    val vm: EditProfileViewModel = koinInject()
    val state by vm.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { vm.clear() }
    }

    LaunchedEffect(Unit) {
        vm.events.collectLatest { onNavigate(it) }
    }

    EditProfileScreen(
        contentPadding = contentPadding,
        state = state,
        onBack = { onNavigate(Screen.Profile) },
        onSave = vm::save,
        onDisplayNameChange = vm::onDisplayNameChange,
        onUsernameChange = vm::onUsernameChange,
        onBioChange = vm::onBioChange,
        onLocationChange = vm::onLocationChange,
        onWebsiteChange = vm::onWebsiteChange
    )
}
