package com.nazam.instaclone.feature.admin.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.feature.admin.presentation.viewmodel.AdminViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminRoute(onBack: () -> Unit) {
    val vm: AdminViewModel = koinViewModel()
    val state by vm.uiState.collectAsState()

    AdminScreen(
        state = state,
        onBack = onBack,
        onQueryChange = vm::onQueryChange,
        onToggle = vm::toggle,
        onRetry = vm::retry,
        onDismissError = vm::dismissError,
        onDismissSnackbar = vm::dismissSnackbar,
    )
}
