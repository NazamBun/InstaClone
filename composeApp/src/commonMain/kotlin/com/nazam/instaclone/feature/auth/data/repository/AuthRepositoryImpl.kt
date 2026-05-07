package com.nazam.instaclone.feature.auth.data.repository

import com.nazam.instaclone.core.utils.safeCall
import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
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
        return try {
            // Roundtrip serveur : si le user a été supprimé/banni ou que le JWT
            // est révoqué, le SDK lève une AuthRestException avec un code clair.
            auth.retrieveUserForCurrentSession()
            buildUser(emailFallback = "")
        } catch (e: AuthRestException) {
            if (e.errorCode in INVALID_SESSION_CODES) {
                // Session morte côté serveur : on purge le JWT local.
                runCatching { auth.signOut() }
                null
            } else {
                runCatching { buildUser(emailFallback = "") }.getOrNull()
            }
        } catch (_: Throwable) {
            // Réseau / 5xx : on garde la session locale pour ne pas casser l'offline.
            runCatching { buildUser(emailFallback = "") }.getOrNull()
        }
    }

    private companion object {
        val INVALID_SESSION_CODES = setOf(
            AuthErrorCode.UserNotFound,
            AuthErrorCode.UserBanned,
            AuthErrorCode.SessionNotFound,
            AuthErrorCode.SessionExpired,
            AuthErrorCode.BadJwt,
            AuthErrorCode.RefreshTokenNotFound,
            AuthErrorCode.RefreshTokenAlreadyUsed
        )
    }
}
