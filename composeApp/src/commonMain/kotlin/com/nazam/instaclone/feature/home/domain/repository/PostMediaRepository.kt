package com.nazam.instaclone.feature.home.domain.repository

/**
 * Contrat "domain" : uploader une image locale et obtenir une URL publique.
 */
interface PostMediaRepository {
    suspend fun uploadPostImage(localUri: String): Result<String>
}