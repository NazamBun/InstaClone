package com.nazam.instaclone.core.di

import org.koin.core.context.startKoin

// ✅ à appeler 1 seule fois au démarrage de l'app
fun initKoin() {
    startKoin {
        modules(appModule)
    }
}