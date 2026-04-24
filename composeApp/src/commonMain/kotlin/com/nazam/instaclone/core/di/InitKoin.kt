package com.nazam.instaclone.core.di

import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(vararg extraModules: Module) {
    try {
        startKoin {
            modules(listOf(appModule) + extraModules)
        }
    } catch (_: KoinApplicationAlreadyStartedException) {
        // Koin est déjà lancé, donc on ne relance pas.
    }
}

fun doInitKoin() {
    initKoin()
}
