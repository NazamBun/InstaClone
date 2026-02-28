package com.nazam.instaclone.core.media

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.nazam.instaclone.BuildConfig
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.UUID

@Composable
actual fun rememberImagePicker(
    onImagePicked: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data

        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val output = UCrop.getOutput(data)
            if (output != null) onImagePicked(output.toString())
        }
    }

    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val dest = createCacheOutputUri(context)
        val intent = buildCropIntent(context, source = uri, destination = dest)
        cropLauncher.launch(intent)
    }

    return remember {
        {
            pickLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

private fun buildCropIntent(
    context: Context,
    source: Uri,
    destination: Uri
): Intent {
    val intent = UCrop.of(source, destination)
        .withAspectRatio(9f, 16f)
        .withMaxResultSize(1080, 1920)
        .getIntent(context)

    // ✅ IMPORTANT : on force NOTRE activity (sinon il ouvre UCropActivity)
    intent.setClass(context, FixedUCropActivity::class.java)

    // ✅ droits pour lire/écrire les URIs
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

    return intent
}

private fun createCacheOutputUri(context: Context): Uri {
    val file = File(context.cacheDir, "crop_${UUID.randomUUID()}.jpg")
    val authority = "${BuildConfig.APPLICATION_ID}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}
