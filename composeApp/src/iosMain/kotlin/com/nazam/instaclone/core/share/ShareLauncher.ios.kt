package com.nazam.instaclone.core.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

@Composable
actual fun rememberShareLauncher(): ShareLauncher {
    return remember {
        object : ShareLauncher {
            override fun share(payload: SharePayload) {
                val items = mutableListOf<Any>(payload.text)

                payload.imagePng?.let { bytes ->
                    val data = bytes.toNSData()
                    val img = UIImage.imageWithData(data)
                    if (img != null) items.add(img)
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

private fun ByteArray.toNSData(): NSData {
    return usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
    }
}
