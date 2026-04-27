package com.nazam.instaclone.feature.auth.data.repository

import com.nazam.instaclone.core.utils.safeCall
import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Implémentation Supabase du AuthRepository.
 *
 * Note : ne gère QUE l'authentification. Les permissions sont dans
 * PermissionsRepository et enrichies par le SessionManager.
 */
class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    private val auth = supabaseClient.auth

    private fun readDisplayName(): String? {
        val user = auth.currentUserOrNull() ?: return null
        return runCatching {
            user.userMetadata?.get("display_name")?.jsonPrimitive?.content
        }.getOrNull()?.takeIf { it.isNotBlank() }
    }

    private fun buildUser(emailFallback: String, displayNameOverride: String? = null): AuthUser {
        val user = auth.currentUserOrNull()
            ?: error("Utilisateur introuvable après authentification")
        return AuthUser(
            id = user.id,
            email = user.email ?: emailFallback,
            displayName = displayNameOverride ?: readDisplayName(),
            canCreatePost = false // ⚠️ enrichi par SessionManager
        )
    }

    override suspend fun login(email: String, password: String): Result<AuthUser> = safeCall {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        buildUser(emailFallback = email)
    }

    override suspend fun signup(
        email: String,
        password: String,
        displayName: String?
    ): Result<AuthUser> = safeCall {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = buildJsonObject {
                if (!displayName.isNullOrBlank()) put("display_name", displayName)
            }
        }
        buildUser(emailFallback = email, displayNameOverride = displayName)
    }

    override suspend fun logout(): Result<Unit> = safeCall { auth.signOut() }

    override suspend fun getCurrentUser(): AuthUser? {
        if (auth.currentUserOrNull() == null) return null
        return runCatching { buildUser(emailFallback = "") }.getOrNull()
    }
}
