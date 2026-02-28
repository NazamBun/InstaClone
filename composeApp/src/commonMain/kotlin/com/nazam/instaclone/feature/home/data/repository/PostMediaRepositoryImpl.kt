package com.nazam.instaclone.feature.home.data.repository

import com.nazam.instaclone.core.media.ImageBytesReader
import com.nazam.instaclone.core.utils.retry
import com.nazam.instaclone.feature.home.domain.repository.PostMediaRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlin.random.Random

/**
 * Upload vers Supabase Storage.
 *
 * IMPORTANT :
 * - Bucket : "posts-images"
 * - Policies : INSERT/SELECT OK
 */
class PostMediaRepositoryImpl(
    private val client: SupabaseClient,
    private val bytesReader: ImageBytesReader
) : PostMediaRepository {

    companion object {
        private const val BUCKET = "posts-images"
    }

    override suspend fun uploadPostImage(localUri: String): Result<String> = runCatching {
        val user = client.auth.currentUserOrNull()
            ?: throw IllegalStateException("AUTH_REQUIRED")

        // ✅ bytes compressés (Android)
        val bytes = bytesReader.readBytes(localUri)

        // ✅ nom unique KMP
        val unique = Random.nextLong().toString().replace("-", "")
        val fileName = "posts/${user.id}/$unique.jpg"

        // ✅ Retry réseau (évite les erreurs temporaires)
        retry(times = 3) {
            client.storage.from(BUCKET).upload(
                path = fileName,
                data = bytes
            ) { upsert = true }
        }

        client.storage.from(BUCKET).publicUrl(fileName)
    }
}
