package com.nazam.instaclone.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

// ✅ à appeler 1 seule fois au démarrage de l'app
fun initKoin(vararg extraModules: Module) {
    startKoin {
        modules(listOf(appModule) + extraModules)
    }
}