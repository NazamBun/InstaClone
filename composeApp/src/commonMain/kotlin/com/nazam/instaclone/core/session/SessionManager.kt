package com.nazam.instaclone.core.session

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Source unique de vérité pour la session.
 * - user == null => déconnecté
 * - user != null => connecté
 *
 * ✅ KMP friendly
 * ✅ Simple
 */
interface SessionManager {
    val user: StateFlow<AuthUser?>
    fun setUser(value: AuthUser?)
    fun refresh()
    fun clear()
}

class DefaultSessionManager(
    private val dispatchers: AppDispatchers,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : SessionManager {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _user = MutableStateFlow<AuthUser?>(null)
    override val user: StateFlow<AuthUser?> = _user

    override fun setUser(value: AuthUser?) {
        _user.value = value
    }

    override fun refresh() {
        scope.launch {
            val current = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
            _user.value = current
        }
    }

    override fun clear() {
        job.cancel()
    }
}