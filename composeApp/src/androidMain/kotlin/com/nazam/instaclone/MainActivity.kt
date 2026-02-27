package com.nazam.instaclone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nazam.instaclone.core.deeplink.DeepLinkStore
import com.nazam.instaclone.core.di.androidPlatformModule
import com.nazam.instaclone.core.di.initKoin

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // ✅ Koin démarre UNE seule fois ici (avec module Android)
        initKoin(androidPlatformModule(this))

        // ✅ Si l'app est ouverte via un lien
        handleDeepLink(intent)

        setContent { App() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // ✅ Si l'app était déjà ouverte et reçoit un nouveau lien
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val url = intent?.dataString ?: return
        DeepLinkStore.onUrlReceived(url)
    }
}
