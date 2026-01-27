package com.nazam.instaclone.feature.home.data.repository

import com.nazam.instaclone.core.media.ImageBytesReader
import com.nazam.instaclone.feature.home.domain.repository.PostMediaRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage

/**
 * Upload vers Supabase Storage.
 *
 * IMPORTANT :
 * - Il faut créer un bucket dans Supabase (ex: "post-images")
 * - Le bucket doit permettre la lecture publique OU tu génères des URLs signées
 */
class PostMediaRepositoryImpl(
    private val client: SupabaseClient,
    private val bytesReader: ImageBytesReader
) : PostMediaRepository {

    companion object {
        private const val BUCKET = "post-images" // ⚠️ à créer dans Supabase
    }

    override suspend fun uploadPostImage(localUri: String): Result<String> = runCatching {
        val user = client.auth.currentUserOrNull()
            ?: throw IllegalStateException("AUTH_REQUIRED")

        val bytes = bytesReader.readBytes(localUri)

        // Nom de fichier unique, simple
        val fileName = "posts/${user.id}/${kotlin.random.Random.nextInt()}.jpg"

        // Upload
        client.storage.from(BUCKET).upload(
            path = fileName,
            data = bytes
        ) {
            upsert = true
        }

        // URL publique
        client.storage.from(BUCKET).publicUrl(fileName)
    }
}