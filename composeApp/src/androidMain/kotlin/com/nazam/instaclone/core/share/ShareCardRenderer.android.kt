package com.nazam.instaclone.core.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.nazam.instaclone.feature.home.domain.model.VsPost
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberShareCardRenderer(): ShareCardRenderer {
    return remember {
        object : ShareCardRenderer {
            override fun renderPng(post: VsPost): ByteArray {
                val size = 1080
                val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bmp)

                canvas.drawColor(Color.rgb(5, 5, 9))

                val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 54f
                    isFakeBoldText = true
                }
                val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 42f
                }
                val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.rgb(255, 78, 184)
                    textSize = 46f
                    isFakeBoldText = true
                }

                fun drawMultiline(text: String, x: Float, startY: Float, paint: Paint, maxLines: Int): Float {
                    val words = text.trim().split(" ")
                    var line = ""
                    var y = startY
                    var lines = 0

                    for (w in words) {
                        val test = if (line.isEmpty()) w else "$line $w"
                        if (paint.measureText(test) > 900f) {
                            canvas.drawText(line, x, y, paint)
                            y += paint.textSize * 1.25f
                            lines++
                            line = w
                            if (lines >= maxLines - 1) break
                        } else {
                            line = test
                        }
                    }

                    if (lines < maxLines) {
                        canvas.drawText(line, x, y, paint)
                        y += paint.textSize * 1.25f
                    }
                    return y
                }

                var y = 140f
                canvas.drawText("VS", 80f, y, accentPaint)
                y += 90f

                y = drawMultiline(post.question, 80f, y, titlePaint, maxLines = 3)
                y += 40f

                canvas.drawText("A) ${post.leftLabel.trim()}", 80f, y, bodyPaint)
                y += 70f
                canvas.drawText("B) ${post.rightLabel.trim()}", 80f, y, bodyPaint)
                y += 90f

                canvas.drawText("Vote dans l'app", 80f, 980f, accentPaint)

                val out = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
                return out.toByteArray()
            }
        }
    }
}
