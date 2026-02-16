package com.nazam.instaclone.core.media

import androidx.compose.runtime.Composable

/**
 * API KMP :
 * commonMain décrit "ce qu'on veut" (ouvrir la galerie).
 * Chaque plateforme (Android / iOS) donne la vraie implémentation.
 *
 * Le callback renvoie une String (uri ou url).
 */
@Composable
expect fun rememberImagePicker(
    onImagePicked: (String) -> Unit
): () -> Unit
