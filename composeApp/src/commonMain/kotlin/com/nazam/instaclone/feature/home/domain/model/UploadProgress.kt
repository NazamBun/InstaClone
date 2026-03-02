package com.nazam.instaclone.feature.home.domain.model

/**
 * Progress d'upload "réel" (0..100%).
 * - On envoie des events au ViewModel pour afficher une vraie barre.
 */
sealed interface UploadProgress {
    data object Reading : UploadProgress
    data class Uploading(val sentBytes: Long, val totalBytes: Long) : UploadProgress
    data class Success(val publicUrl: String) : UploadProgress
    data class Error(val message: String) : UploadProgress
}

fun UploadProgress.Uploading.percentOrNull(): Int? {
    if (totalBytes <= 0) return null
    val p = (sentBytes * 100.0 / totalBytes).toInt()
    return p.coerceIn(0, 100)
}
