package com.nazam.instaclone.feature.admin.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.admin.domain.model.AdminUser
import com.nazam.instaclone.feature.admin.presentation.model.AdminUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.action_retry
import instaclone.composeapp.generated.resources.admin_grant_permission
import instaclone.composeapp.generated.resources.admin_no_users
import instaclone.composeapp.generated.resources.admin_screen_title
import instaclone.composeapp.generated.resources.admin_search_hint
import instaclone.composeapp.generated.resources.nav_back_cd
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    state: AdminUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onToggle: (userId: String, allow: Boolean) -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
    onDismissSnackbar: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarText = state.snackbarMessage?.asString()
    val errorText = state.error?.asString()

    if (snackbarText != null) {
        LaunchedEffect(snackbarText) {
            snackbarHostState.showSnackbar(snackbarText)
            onDismissSnackbar()
        }
    }
    if (errorText != null && state.users.isNotEmpty()) {
        LaunchedEffect(errorText) {
            snackbarHostState.showSnackbar(errorText)
            onDismissError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.admin_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(Res.string.nav_back_cd),
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 80.dp),
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = { Text(stringResource(Res.string.admin_search_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when {
                    state.isLoading && state.users.isEmpty() -> CircularProgressIndicator()
                    state.users.isEmpty() && state.error != null -> AdminErrorState(state.error, onRetry)
                    state.users.isEmpty() -> Text(stringResource(Res.string.admin_no_users))
                    else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.users, key = AdminUser::id) { user ->
                            AdminUserRow(
                                user = user,
                                isToggling = state.togglingUserId == user.id,
                                onToggle = { allow -> onToggle(user.id, allow) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUserRow(user: AdminUser, isToggling: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.displayName ?: user.id)
            Text(
                text = stringResource(Res.string.admin_grant_permission),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (isToggling) CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
        else Switch(checked = user.canCreatePost, onCheckedChange = onToggle)
    }
}

@Composable
private fun AdminErrorState(error: UiText, onRetry: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(error.asString())
        Button(onClick = onRetry) { Text(stringResource(Res.string.action_retry)) }
    }
}
