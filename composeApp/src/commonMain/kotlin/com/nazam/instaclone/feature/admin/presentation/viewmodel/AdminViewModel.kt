package com.nazam.instaclone.feature.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.admin.domain.usecase.GrantPostPermissionUseCase
import com.nazam.instaclone.feature.admin.domain.usecase.ListUsersUseCase
import com.nazam.instaclone.feature.admin.domain.usecase.RevokePostPermissionUseCase
import com.nazam.instaclone.feature.admin.presentation.model.AdminUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.admin_error_generic
import instaclone.composeapp.generated.resources.admin_error_not_admin
import instaclone.composeapp.generated.resources.admin_toggle_success_grant
import instaclone.composeapp.generated.resources.admin_toggle_success_revoke
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SEARCH_DEBOUNCE_MS = 300L

class AdminViewModel(
    private val dispatchers: AppDispatchers,
    private val listUsersUseCase: ListUsersUseCase,
    private val grantPostPermissionUseCase: GrantPostPermissionUseCase,
    private val revokePostPermissionUseCase: RevokePostPermissionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = withContext(dispatchers.io) {
                listUsersUseCase.execute(query = _uiState.value.query)
            }
            result
                .onSuccess { users ->
                    _uiState.update { it.copy(users = users, isLoading = false, error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = mapError(e)) }
                }
        }
    }

    fun onQueryChange(q: String) {
        _uiState.update { it.copy(query = q) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            load()
        }
    }

    fun toggle(userId: String, allow: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(togglingUserId = userId, error = null) }
            val result = withContext(dispatchers.io) {
                if (allow) grantPostPermissionUseCase.execute(userId)
                else revokePostPermissionUseCase.execute(userId)
            }
            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            togglingUserId = null,
                            snackbarMessage = UiText.Resource(
                                if (allow) Res.string.admin_toggle_success_grant
                                else Res.string.admin_toggle_success_revoke
                            ),
                        )
                    }
                    load()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(togglingUserId = null, error = mapError(e)) }
                }
        }
    }

    fun retry() = load()
    fun dismissError() = _uiState.update { it.copy(error = null) }
    fun dismissSnackbar() = _uiState.update { it.copy(snackbarMessage = null) }

    private fun mapError(e: Throwable): UiText {
        val msg = e.message?.lowercase().orEmpty()
        return if ("not_admin" in msg || "42501" in msg) {
            UiText.Resource(Res.string.admin_error_not_admin)
        } else {
            UiText.Resource(Res.string.admin_error_generic)
        }
    }
}
