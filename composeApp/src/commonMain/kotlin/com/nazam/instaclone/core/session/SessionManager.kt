package com.nazam.instaclone.core.session

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.permissions.domain.usecase.GetPostPermissionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Source unique de vérité pour la session.
 * - user == null => déconnecté
 * - user != null => connecté (avec ses permissions enrichies)
 */
interface SessionManager {
    val user: StateFlow<AuthUser?>
    suspend fun setUser(value: AuthUser?)
    fun refresh()
    fun clear()
}

class DefaultSessionManager(
    private val dispatchers: AppDispatchers,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getPostPermissionUseCase: GetPostPermissionUseCase
) : SessionManager {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _user = MutableStateFlow<AuthUser?>(null)
    override val user: StateFlow<AuthUser?> = _user

    override suspend fun setUser(value: AuthUser?) {
        _user.value = value?.let { enrichWithPermissions(it) }
    }

    override fun refresh() {
        scope.launch {
            val current = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
            _user.value = current?.let { enrichWithPermissions(it) }
        }
    }

    override fun clear() {
        job.cancel()
    }

    /**
     * Enrichit le user avec ses permissions (canCreatePost, etc.).
     * Si l'appel échoue, on garde des permissions "Denied" par défaut.
     */
    private suspend fun enrichWithPermissions(user: AuthUser): AuthUser {
        val permission = withContext(dispatchers.io) {
            getPostPermissionUseCase.execute(user.id)
        }.getOrNull()

        return user.copy(canCreatePost = permission?.canCreatePost == true)
    }
}
