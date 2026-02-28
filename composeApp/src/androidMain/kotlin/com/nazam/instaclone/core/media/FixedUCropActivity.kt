package com.nazam.instaclone.core.media

import android.os.Bundle
import androidx.core.view.WindowCompat
import com.yalantis.ucrop.UCropActivity

/**
 * uCrop + edge-to-edge : parfois l'UI passe sous la status bar.
 * Ici on désactive edge-to-edge pour CETTE activity seulement.
 */
class FixedUCropActivity : UCropActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ uCrop doit "fit" sous les barres système
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }
}
