package com.nazam.instaclone.feature.auth.data.repository


import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.auth.data.mapper.AuthMapper
import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepositoryImpl(
    // On injecte le client Supabase (facile à tester / remplacer plus tard)
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) : AuthRepository {

    private val auth = supabaseClient.auth

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        return runCatching {
            // 1. Demander à Supabase de connecter l'utilisateur
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // 2. Récupérer l'utilisateur courant
            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilisateur introuvable après le login")

            // 3. Mapper vers le modèle de domaine
            AuthMapper.toDomain(
                id = user.id,
                // si user.email est null, on prend l'email saisi
                email = user.email ?: email,
                displayName = null // on gèrera plus tard avec les métadonnées / table profil
            )
        }
    }

    override suspend fun signup(
        email: String,
        password: String,
        displayName: String?
    ): Result<AuthUser> {
        return runCatching {
            // 1. Création du compte Supabase
            val createdUser = auth.signUpWith(Email) {
                this.email = email
                this.password = password
                // plus tard on pourra ajouter des metadata dans "data = ..."
            }

            // createdUser peut être null selon la config Supabase (confirm email, etc.)
            val user = createdUser ?: auth.currentUserOrNull()
            ?: throw IllegalStateException("Utilisateur introuvable après l'inscription")

            // 2. Mapper vers domaine
            AuthMapper.toDomain(
                id = user.id,
                email = user.email ?: email,
                displayName = displayName
            )
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            auth.signOut()
        }
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