package com.nazam.instaclone.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface AppDispatchers {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}

class DefaultAppDispatchers : AppDispatchers {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default

    // ✅ KMP: sur iOS, Dispatchers.IO peut être indisponible -> on utilise Default
    override val io: CoroutineDispatcher = Dispatchers.Default
}