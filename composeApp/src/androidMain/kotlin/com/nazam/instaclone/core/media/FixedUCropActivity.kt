package com.nazam.instaclone.core.media

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.yalantis.ucrop.UCropActivity

/**
 * Fix : uCrop est parfois "collé" sous la status bar (edge-to-edge).
 * Ici on force un paddingTop = hauteur status bar.
 */
class FixedUCropActivity : UCropActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ on veut que le système nous donne les insets
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val content = findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(content) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())

            // ✅ on pousse tout vers le bas (status bar)
            v.setPadding(
                v.paddingLeft,
                statusBars.top,
                v.paddingRight,
                v.paddingBottom
            )

            insets
        }

        // Force l'application des insets
        ViewCompat.requestApplyInsets(content)
    }
}
