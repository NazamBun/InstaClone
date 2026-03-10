package com.nazam.instaclone.feature.auth.data.repository

import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) : AuthRepository {

    private companion object {
        const val POST_PERMISSIONS_TABLE = "post_permissions"
    }

    private val auth = supabaseClient.auth
    private val json = Json { ignoreUnknownKeys = true }

    private fun readDisplayNameFromMetadata(): String? {
        val user = auth.currentUserOrNull() ?: return null
        return runCatching {
            user.userMetadata
                ?.get("display_name")
                ?.jsonPrimitive
                ?.content
        }.getOrNull()?.takeIf { it.isNotBlank() }
    }

    private suspend fun readCanCreatePost(userId: String): Boolean {
        val response = supabaseClient
            .postgrest[POST_PERMISSIONS_TABLE]
            .select {
                filter { eq("user_id", userId) }
                limit(1)
            }

        val item = json
            .parseToJsonElement(response.data)
            .jsonArray
            .firstOrNull()
            ?.jsonObject
            ?: return false

        return item["can_create_post"]
            ?.jsonPrimitive
            ?.booleanOrNull == true
    }

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        return runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilisateur introuvable après le login")

            AuthUser(
                id = user.id,
                email = user.email ?: email,
                displayName = readDisplayNameFromMetadata(),
                canCreatePost = readCanCreatePost(user.id)
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
                this.data = buildJsonObject {
                    if (!displayName.isNullOrBlank()) {
                        put("display_name", displayName)
                    }
                }
            }

            val user = createdUser ?: auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilisateur introuvable après l'inscription")

            AuthUser(
                id = user.id,
                email = user.email ?: email,
                displayName = displayName,
                canCreatePost = readCanCreatePost(user.id)
            )
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching { auth.signOut() }
    }

    override suspend fun getCurrentUser(): AuthUser? {
        val user = auth.currentUserOrNull() ?: return null

        return AuthUser(
            id = user.id,
            email = user.email ?: "",
            displayName = readDisplayNameFromMetadata(),
            canCreatePost = readCanCreatePost(user.id)
        )
    }
}
