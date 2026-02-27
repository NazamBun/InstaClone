package com.nazam.instaclone.core.clipboard

import androidx.compose.runtime.Composable

/**
 * Copie du texte dans le presse-papier.
 * ✅ KMP friendly (expect/actual)
 */
interface ClipboardManager {
    fun setText(text: String)
}

@Composable
expect fun rememberClipboardManager(): ClipboardManager
