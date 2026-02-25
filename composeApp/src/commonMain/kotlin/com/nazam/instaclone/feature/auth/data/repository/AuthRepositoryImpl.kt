package com.nazam.instaclone.feature.auth.data.repository

import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.auth.data.mapper.AuthMapper
import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) : AuthRepository {

    companion object {
        private const val ERROR_INVITE_REQUIRED = "INVITE_REQUIRED"
        private const val RPC_CAN_SIGNUP = "can_signup"
        private const val RPC_CONSUME_INVITE = "consume_invite"
    }

    private val auth = supabaseClient.auth

    private fun readDisplayNameFromMetadata(): String? {
        val user = auth.currentUserOrNull() ?: return null
        return runCatching {
            user.userMetadata
                ?.get("display_name")
                ?.jsonPrimitive
                ?.content
        }.getOrNull()?.takeIf { it.isNotBlank() }
    }

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        return runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilisateur introuvable après le login")

            val displayName = readDisplayNameFromMetadata()

            AuthMapper.toDomain(
                id = user.id,
                email = user.email ?: email,
                displayName = displayName
            )
        }
    }

    override suspend fun signup(
        email: String,
        password: String,
        displayName: String?
    ): Result<AuthUser> {
        return runCatching {
            // ✅ Invite-only : on vérifie côté Supabase (RPC)
            val canSignup = supabaseClient.postgrest.rpc(
                function = RPC_CAN_SIGNUP,
                parameters = buildJsonObject { put("p_email", email) }
            ).data.toBooleanStrictOrNull() ?: false

            if (!canSignup) throw IllegalStateException(ERROR_INVITE_REQUIRED)

            val createdUser = auth.signUpWith(Email) {
                this.email = email
                this.password = password

                // metadata : utile si tu as un trigger SQL profiles.display_name
                this.data = buildJsonObject {
                    if (!displayName.isNullOrBlank()) {
                        put("display_name", displayName)
                    }
                }
            }

            // ✅ Après inscription OK : on consomme l'invite (best-effort)
            runCatching {
                supabaseClient.postgrest.rpc(
                    function = RPC_CONSUME_INVITE,
                    parameters = buildJsonObject { put("p_email", email) }
                )
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
        val displayName = readDisplayNameFromMetadata()

        return AuthMapper.toDomain(
            id = user.id,
            email = user.email ?: "",
            displayName = displayName
        )
    }
}
