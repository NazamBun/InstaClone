package com.nazam.instaclone.core.media

import androidx.compose.runtime.Composable

/**
 * iOS : on le fera plus tard.
 * Pour l'instant : no-op (ne fait rien).
 */
@Composable
actual fun rememberImagePicker(
    onImagePicked: (String) -> Unit
): () -> Unit = { }
