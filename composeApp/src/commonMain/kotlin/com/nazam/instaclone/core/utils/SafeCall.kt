package com.nazam.instaclone.core.utils

import kotlinx.coroutines.CancellationException

/**
 * Remplacement sûr de runCatching pour les coroutines.
 *
 * Pourquoi ?
 * - runCatching attrape TOUTES les Throwable, y compris CancellationException.
 * - Or, CancellationException doit toujours remonter pour que les coroutines
 *   puissent être annulées proprement (rotation, scope cancel, etc.).
 *
 * Usage :
 *   suspend fun login(...) : Result<AuthUser> = safeCall { ... }
 */
inline fun <T> safeCall(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}
