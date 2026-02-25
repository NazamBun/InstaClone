package com.nazam.instaclone.core.share

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberShareLauncher(): ShareLauncher {
    val context = LocalContext.current

    return remember {
        object : ShareLauncher {
            override fun share(payload: SharePayload) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, payload.text)
                    payload.subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
                }
                context.startActivity(Intent.createChooser(intent, null))
            }
        }
    }
}
