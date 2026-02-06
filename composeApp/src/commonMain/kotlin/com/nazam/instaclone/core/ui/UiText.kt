package com.nazam.instaclone.core.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Texte UI propre (KMP friendly).
 * - DynamicString : texte direct (ex: message serveur)
 * - Resource : texte venant de composeResources (traduisible)
 * - ResourceArgs : Resource + paramètres (%1$d, %1$s...)
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    data class Resource(val res: StringResource) : UiText()
    data class ResourceArgs(val res: StringResource, val args: List<Any>) : UiText()
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.Resource -> stringResource(res)
    is UiText.ResourceArgs -> stringResource(res, *args.toTypedArray())
}