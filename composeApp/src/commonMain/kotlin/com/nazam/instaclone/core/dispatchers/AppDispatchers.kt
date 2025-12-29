package com.nazam.instaclone.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * ✅ Petit "provider" pour les dispatchers.
 * But : ne pas écrire Dispatchers.Default / Main partout.
 * Plus testable + plus propre en KMP.
 */
interface AppDispatchers {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}

/**
 * ✅ Impl par défaut (prod)
 */
object DefaultAppDispatchers : AppDispatchers {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default
}