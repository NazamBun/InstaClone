package com.nazam.instaclone.core.access

import com.nazam.instaclone.feature.auth.domain.model.AuthUser

/**
 * Règle simple : qui a le droit de créer un VS ?
 *
 * V1 :
 * - seulement toi (creator)
 * - plus tard : tu ajoutes d'autres emails dans ALLOWED_EMAILS
 *
 * ✅ KMP friendly
 * ✅ Simple
 */
object CreatePostAccess {

    // ⚠️ Mets TON email de compte ici (celui de Supabase / Auth).
    private const val CREATOR_EMAIL = "bun.nazam@gmail.com"

    private val ALLOWED_EMAILS: Set<String> = setOf(
        CREATOR_EMAIL
        // Plus tard : "ami@email.com",
        // "vip@email.com",
    )

    fun canCreate(user: AuthUser?): Boolean {
        val email = user?.email?.trim()?.lowercase().orEmpty()
        return email.isNotBlank() && email in ALLOWED_EMAILS
    }
}
