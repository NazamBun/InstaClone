package com.nazam.instaclone.core.utils

import kotlinx.coroutines.delay

/**
 * Retry simple :
 * - tentatives: 3 par défaut
 * - délai progressif: 300ms, 600ms, 1200ms...
 */
suspend fun <T> retry(
    times: Int = 3,
    initialDelayMs: Long = 300,
    block: suspend () -> T
): T {
    var lastError: Throwable? = null
    var delayMs = initialDelayMs

    repeat(times) { attempt ->
        try {
            return block()
        } catch (t: Throwable) {
            lastError = t
            if (attempt == times - 1) throw t
            delay(delayMs)
            delayMs *= 2
        }
    }

    throw lastError ?: IllegalStateException("retry failed")
}
