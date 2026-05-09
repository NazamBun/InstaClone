package com.nazam.instaclone.core.di

import com.nazam.instaclone.core.media.ImageBytesReader
import com.nazam.instaclone.core.media.IosImageBytesReader
import org.koin.dsl.module

/**
 * Module iOS : fournit le reader iOS.
 * Override le binding par défaut (DefaultImageBytesReader) du appModule.
 */
fun iosPlatformModule() = module {
    single<ImageBytesReader> { IosImageBytesReader() }
}
