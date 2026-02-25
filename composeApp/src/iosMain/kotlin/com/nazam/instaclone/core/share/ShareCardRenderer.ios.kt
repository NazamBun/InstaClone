package com.nazam.instaclone.core.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * iOS V1 (simple) :
 * Pour l'instant on ne génère pas d'image (PNG) côté iOS.
 * On garde le partage texte + lien (très bien déjà).
 *
 * ✅ KMP friendly
 * ✅ Compile safe
 */
@Composable
actual fun rememberShareCardRenderer(): ShareCardRenderer {
    return remember {
        object : ShareCardRenderer {
            override fun renderPng(post: VsPost): ByteArray = ByteArray(0)
        }
    }
}
