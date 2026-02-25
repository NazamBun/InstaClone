package com.nazam.instaclone.core.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberShareLauncher(): ShareLauncher {
    return remember {
        object : ShareLauncher {
            override fun share(payload: SharePayload) {
                val controller = UIActivityViewController(
                    activityItems = listOf(payload.text),
                    applicationActivities = null
                )
                val root = UIApplication.sharedApplication.keyWindow?.rootViewController
                root?.presentViewController(controller, animated = true, completion = null)
            }
        }
    }
}
