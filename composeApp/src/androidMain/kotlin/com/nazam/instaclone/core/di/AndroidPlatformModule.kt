package com.nazam.instaclone.core.di

import android.content.Context
import com.nazam.instaclone.core.media.AndroidImageBytesReader
import com.nazam.instaclone.core.media.ImageBytesReader
import org.koin.dsl.module

/**
 * Module Android : fournit le reader Android avec Context.
 */
fun androidPlatformModule(appContext: Context) = module {
    single<ImageBytesReader> { AndroidImageBytesReader(appContext.applicationContext) }
}