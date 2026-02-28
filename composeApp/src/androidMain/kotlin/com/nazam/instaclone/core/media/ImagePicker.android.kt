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
import com.nazam.instaclone.R
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import java.io.File
import java.util.UUID

/**
 * Android :
 * 1) Pick image (Photo Picker)
 * 2) Crop (uCrop) avec design pro + zoom/rotate
 */
@Composable
actual fun rememberImagePicker(
    onImagePicked: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data ?: return@rememberLauncherForActivityResult
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult

        UCrop.getOutput(data)?.let { onImagePicked(it.toString()) }
    }

    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val dest = createCacheOutputUri(context)
        cropLauncher.launch(buildCropIntent(context, source = uri, destination = dest))
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
    val options = UCrop.Options().apply {
        // ✅ UI pro
        setToolbarTitle("Modifier la photo")
        setStatusBarColor(context.getColorCompat(R.color.ucrop_black))
        setToolbarColor(context.getColorCompat(R.color.ucrop_black))
        setActiveControlsWidgetColor(context.getColorCompat(R.color.ucrop_accent))
        setToolbarWidgetColor(context.getColorCompat(R.color.ucrop_white))
        setRootViewBackgroundColor(context.getColorCompat(R.color.ucrop_dark))

        // ✅ montrer le bas (zoom / rotate / etc.)
        setHideBottomControls(false)

        // ✅ grille et cadre
        setShowCropGrid(true)
        setShowCropFrame(true)

        // ✅ gestes autorisés
        // (scale tab, rotate tab, aspect ratio tab)
        setAllowedGestures(
            UCropActivity.SCALE,
            UCropActivity.ROTATE,
            UCropActivity.ALL
        )
    }

    val intent = UCrop.of(source, destination)
        .withAspectRatio(9f, 16f)
        .withMaxResultSize(1080, 1920)
        .withOptions(options)
        .getIntent(context)

    // ✅ IMPORTANT : on force NOTRE activity (sinon UCropActivity)
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

private fun Context.getColorCompat(resId: Int): Int =
    resources.getColor(resId, theme)
