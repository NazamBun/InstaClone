package com.nazam.instaclone.feature.auth.data.repository

import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.auth.data.mapper.AuthMapper
import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) : AuthRepository {

    private val auth = supabaseClient.auth

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        return runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilisateur introuvable après le login")

            AuthMapper.toDomain(
                id = user.id,
                email = user.email ?: email,
                displayName = null
            )
        }
    }

    override suspend fun signup(
        email: String,
        password: String,
        displayName: String?
    ): Result<AuthUser> {
        return runCatching {
            val createdUser = auth.signUpWith(Email) {
                this.email = email
                this.password = password

                // ✅ metadata : utilisé par le trigger SQL pour remplir profiles.display_name
                this.data = buildJsonObject {
                    if (!displayName.isNullOrBlank()) {
                        put("display_name", displayName)
                    }
                }
            }

            val user = createdUser ?: auth.currentUserOrNull()
            ?: throw IllegalStateException("Utilisateur introuvable après l'inscription")

            AuthMapper.toDomain(
                id = user.id,
                email = user.email ?: email,
                displayName = displayName
            )
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching { auth.signOut() }
    }

    override suspend fun getCurrentUser(): AuthUser? {
        val user = auth.currentUserOrNull() ?: return null

        return AuthMapper.toDomain(
            id = user.id,
            email = user.email ?: "",
            displayName = null
        )
    }
}