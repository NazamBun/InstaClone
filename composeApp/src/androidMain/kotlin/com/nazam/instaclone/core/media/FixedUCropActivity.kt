package com.nazam.instaclone.core.media

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.yalantis.ucrop.UCropActivity

/**
 * Fix uCrop + edge-to-edge :
 * - "fit system windows" AVANT que uCrop pose son layout
 * - paddingTop = hauteur status bar
 */
class FixedUCropActivity : UCropActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ AVANT super.onCreate()
        WindowCompat.setDecorFitsSystemWindows(window, true)

        super.onCreate(savedInstanceState)

        val content = findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(content) { v, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(v.paddingLeft, top, v.paddingRight, v.paddingBottom)
            insets
        }

        ViewCompat.requestApplyInsets(content)
    }
}
