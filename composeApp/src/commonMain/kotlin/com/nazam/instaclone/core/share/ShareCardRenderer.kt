package com.nazam.instaclone.core.share

import androidx.compose.runtime.Composable
import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * Génère une image "carte" (PNG) pour rendre le partage plus viral.
 * ✅ KMP friendly : actual Android / iOS
 */
interface ShareCardRenderer {
    fun renderPng(post: VsPost): ByteArray
}

@Composable
expect fun rememberShareCardRenderer(): ShareCardRenderer
