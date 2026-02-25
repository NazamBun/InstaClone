package com.nazam.instaclone.core.share

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberShareLauncher(): ShareLauncher {
    val context = LocalContext.current

    return remember {
        object : ShareLauncher {
            override fun share(payload: SharePayload) {
                val imageBytes = payload.imagePng

                val intent = if (imageBytes == null) {
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, payload.text)
                        payload.subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
                    }
                } else {
                    val dir = File(context.cacheDir, "shares").apply { mkdirs() }
                    val file = File(dir, payload.imageFileName)
                    file.writeBytes(imageBytes)

                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )

                    Intent(Intent.ACTION_SEND).apply {
                        type = payload.imageMimeType
                        putExtra(Intent.EXTRA_TEXT, payload.text)
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        payload.subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
                    }
                }

                context.startActivity(Intent.createChooser(intent, null))
            }
        }
    }
}
