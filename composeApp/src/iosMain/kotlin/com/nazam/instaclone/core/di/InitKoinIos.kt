package com.nazam.instaclone.core.di

/**
 * Point d'entrée d'initialisation Koin pour iOS.
 * Appelé depuis Swift (iOSApp.init) pour enregistrer appModule + iosPlatformModule.
 * Le vararg de initKoin permet d'ajouter le binding IosImageBytesReader
 * (équivalent symétrique d'androidPlatformModule passé depuis MainActivity).
 */
fun doInitKoinIos() {
    initKoin(iosPlatformModule())
}
