package com.nazam.instaclone.core.share

import androidx.compose.runtime.Composable

/**
 * Lance le partage natif (Android/iOS).
 * ✅ MVVM: appelé depuis l'UI (pas le ViewModel)
 */
interface ShareLauncher {
    fun share(payload: SharePayload)
}

@Composable
expect fun rememberShareLauncher(): ShareLauncher
