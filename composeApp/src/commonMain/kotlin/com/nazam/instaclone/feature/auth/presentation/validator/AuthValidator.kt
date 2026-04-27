package com.nazam.instaclone.feature.auth.presentation.validator

import com.nazam.instaclone.core.ui.UiText
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.error_auth_email_invalid_format
import instaclone.composeapp.generated.resources.error_auth_password_min_length
import instaclone.composeapp.generated.resources.error_auth_password_mismatch
import instaclone.composeapp.generated.resources.error_email_password_required

/**
 * Validation côté client des entrées d'authentification.
 * Centralise les règles pour Login / Signup.
 */
object AuthValidator {

    private const val PASSWORD_MIN_LENGTH = 6
    private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    fun validateLogin(email: String, password: String): UiText? = when {
        email.isBlank() || password.isBlank() ->
            UiText.Resource(Res.string.error_email_password_required)
        !EMAIL_REGEX.matches(email) ->
            UiText.Resource(Res.string.error_auth_email_invalid_format)
        else -> null
    }

    fun validateSignup(
        email: String,
        password: String,
        confirmPassword: String
    ): UiText? = when {
        email.isBlank() || password.isBlank() ->
            UiText.Resource(Res.string.error_email_password_required)
        !EMAIL_REGEX.matches(email) ->
            UiText.Resource(Res.string.error_auth_email_invalid_format)
        password.length < PASSWORD_MIN_LENGTH ->
            UiText.Resource(Res.string.error_auth_password_min_length)
        password != confirmPassword ->
            UiText.Resource(Res.string.error_auth_password_mismatch)
        else -> null
    }
}
