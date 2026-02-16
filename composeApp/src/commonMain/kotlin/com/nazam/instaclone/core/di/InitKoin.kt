package com.nazam.instaclone.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * ✅ À appeler 1 seule fois au démarrage de l'app.
 * - Android peut appeler initKoin(...)
 * - iOS va appeler doInitKoin() (plus simple côté Swift)
 */
fun initKoin(vararg extraModules: Module) {
    startKoin {
        modules(listOf(appModule) + extraModules)
    }
}

/**
 * ✅ Wrapper iOS-friendly.
 * Swift peut appeler : InitKoinKt.doInitKoin()
 * (et plus tard si besoin : InitKoinKt.doInitKoin(extraModules: []))
 */
fun doInitKoin() {
    initKoin()
}
