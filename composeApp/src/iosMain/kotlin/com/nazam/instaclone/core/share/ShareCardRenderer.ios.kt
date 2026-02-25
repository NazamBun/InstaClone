package com.nazam.instaclone.core.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.nazam.instaclone.feature.home.domain.model.VsPost
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.*
import platform.darwin.NSObject

@Composable
actual fun rememberShareCardRenderer(): ShareCardRenderer {
    return remember {
        object : ShareCardRenderer {
            override fun renderPng(post: VsPost): ByteArray {
                val size = 1080.0
                val rect = CGRectMake(0.0, 0.0, size, size)

                UIGraphicsBeginImageContextWithOptions(rect.size, true, 1.0)
                val ctx = UIGraphicsGetCurrentContext()!!

                // background
                UIColor.colorWithRed(5.0 / 255.0, green = 5.0 / 255.0, blue = 9.0 / 255.0, alpha = 1.0).setFill()
                UIRectFill(rect)

                val accent = UIColor.colorWithRed(1.0, green = 78.0 / 255.0, blue = 184.0 / 255.0, alpha = 1.0)

                fun draw(text: String, x: Double, y: Double, size: Double, bold: Boolean, color: UIColor) {
                    val font = if (bold) UIFont.boldSystemFontOfSize(size) else UIFont.systemFontOfSize(size)
                    val attrs = mapOf(
                        NSAttributedStringKeyFont to font,
                        NSAttributedStringKeyForegroundColor to color
                    )
                    (text as NSString).drawAtPoint(CGPointMake(x, y), withAttributes = attrs)
                }

                draw("VS", 80.0, 80.0, 46.0, true, accent)
                draw(post.question.trim(), 80.0, 180.0, 44.0, true, UIColor.whiteColor)

                draw("A) ${post.leftLabel.trim()}", 80.0, 380.0, 40.0, false, UIColor.whiteColor)
                draw("B) ${post.rightLabel.trim()}", 80.0, 450.0, 40.0, false, UIColor.whiteColor)

                draw("Vote dans l'app", 80.0, 980.0, 44.0, true, accent)

                val image = UIGraphicsGetImageFromCurrentImageContext()
                UIGraphicsEndImageContext()

                val pngData = UIImagePNGRepresentation(image!!)!!
                val bytes = pngData.bytes!!
                val length = pngData.length.toInt()

                return ByteArray(length).also { out ->
                    out.usePinned { pinned ->
                        platform.posix.memcpy(pinned.addressOf(0), bytes, length.toULong())
                    }
                }
            }
        }
    }
}
