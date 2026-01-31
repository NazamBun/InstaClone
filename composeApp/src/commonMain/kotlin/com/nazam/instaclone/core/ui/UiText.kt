package com.nazam.instaclone.core.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Texte UI propre (KMP friendly).
 * - DynamicString : texte direct (ex: message serveur)
 * - Resource : texte venant de composeResources (traduisible)
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    data class Resource(val res: StringResource) : UiText()
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.Resource -> stringResource(res)
}