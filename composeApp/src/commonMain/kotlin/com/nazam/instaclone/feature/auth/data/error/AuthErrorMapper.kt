package com.nazam.instaclone.feature.auth.data.error

import com.nazam.instaclone.core.ui.UiText
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.error_auth_email_already_used
import instaclone.composeapp.generated.resources.error_auth_invalid_credentials
import instaclone.composeapp.generated.resources.error_auth_invalid_email
import instaclone.composeapp.generated.resources.error_auth_network
import instaclone.composeapp.generated.resources.error_auth_password_too_short
import instaclone.composeapp.generated.resources.error_auth_user_not_found
import instaclone.composeapp.generated.resources.error_invite_required
import instaclone.composeapp.generated.resources.error_unknown

/**
 * Mappe les erreurs Supabase (souvent en anglais) vers des UiText traduits.
 * Évite d'exposer des messages bruts à l'utilisateur.
 */
object AuthErrorMapper {

    fun map(error: Throwable): UiText {
        val msg = error.message?.lowercase().orEmpty()
        return when {
            "invalid login credentials" in msg -> ui(Res.string.error_auth_invalid_credentials)
            "invalid email" in msg -> ui(Res.string.error_auth_invalid_email)
            "user not found" in msg -> ui(Res.string.error_auth_user_not_found)
            "already registered" in msg ||
                "user already" in msg -> ui(Res.string.error_auth_email_already_used)
            "password should be at least" in msg ||
                "password is too short" in msg -> ui(Res.string.error_auth_password_too_short)
            "not invited" in msg ||
                "invite" in msg -> ui(Res.string.error_invite_required)
            "network" in msg ||
                "timeout" in msg ||
                "unable to resolve" in msg -> ui(Res.string.error_auth_network)
            else -> ui(Res.string.error_unknown)
        }
    }

    private fun ui(res: org.jetbrains.compose.resources.StringResource) = UiText.Resource(res)
}
