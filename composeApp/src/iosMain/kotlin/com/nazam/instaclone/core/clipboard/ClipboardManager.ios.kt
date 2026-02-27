package com.nazam.instaclone.core.clipboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIPasteboard

@Composable
actual fun rememberClipboardManager(): ClipboardManager {
    return remember {
        object : ClipboardManager {
            override fun setText(text: String) {
                UIPasteboard.generalPasteboard.string = text
            }
        }
    }
}
