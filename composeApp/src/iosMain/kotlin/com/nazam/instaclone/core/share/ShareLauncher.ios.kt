package com.nazam.instaclone.core.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

/**
 * iOS share:
 * - texte toujours
 * - image optionnelle (PNG) si fournie
 *
 * ✅ KMP friendly
 * ✅ Simple et stable
 */
@Composable
actual fun rememberShareLauncher(): ShareLauncher {
    return remember {
        object : ShareLauncher {
            override fun share(payload: SharePayload) {
                val items = buildList<Any> {
                    add(payload.text)

                    // Image optionnelle
                    val png = payload.imagePng
                    if (png != null && png.isNotEmpty()) {
                        add(png.toNSData())
                    }
                }

                val controller = UIActivityViewController(
                    activityItems = items,
                    applicationActivities = null
                )

                val root = UIApplication.sharedApplication.keyWindow?.rootViewController
                root?.presentViewController(controller, animated = true, completion = null)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    return usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
    }
}
