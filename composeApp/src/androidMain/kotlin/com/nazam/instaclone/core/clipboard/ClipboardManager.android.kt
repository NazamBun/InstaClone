package com.nazam.instaclone.core.clipboard

import android.content.ClipData
import android.content.ClipboardManager as AndroidClipboardManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberClipboardManager(): ClipboardManager {
    val context = LocalContext.current

    return remember {
        object : ClipboardManager {
            override fun setText(text: String) {
                val clipboard = context.getSystemService(AndroidClipboardManager::class.java)
                clipboard?.setPrimaryClip(ClipData.newPlainText("text", text))
            }
        }
    }
}
