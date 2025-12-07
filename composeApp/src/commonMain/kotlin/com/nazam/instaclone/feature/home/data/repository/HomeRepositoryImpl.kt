package com.nazam.instaclone.feature.home.data.repository

import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class HomeRepositoryImpl(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : HomeRepository {

    // ⚠️ le vrai nom de ta table Supabase
    private val TABLE = "posts"

    // Petit cache en mémoire pour gérer les votes localement
    private val cache = mutableListOf<VsPost>()

    override suspend fun getFeed(): Result<List<VsPost>> =
        runCatching {
            // 1) Appel Supabase : SELECT * FROM posts
            val result: PostgrestResult = client
                .postgrest[TABLE]
                .select()

            // 2) JSON -> List<PostDto>
            val dtoList: List<PostDto> = Json {
                ignoreUnknownKeys = true // on ignore les colonnes en plus
            }.decodeFromString(
                deserializer = ListSerializer(PostDto.serializer()),
                string = result.data
            )

            // 3) DTO -> VsPost
            val posts = dtoList.map { it.toDomain() }

            // 4) on met à jour le cache
            cache.clear()
            cache.addAll(posts)

            posts
        }

    // 🔹 Vote gauche : on modifie seulement le cache (pas encore Supabase)
    override suspend fun voteLeft(postId: String): Result<VsPost> =
        runCatching {
            // Si le cache est vide, on recharge une fois
            if (cache.isEmpty()) {
                getFeed().getOrThrow()
            }

            val index = cache.indexOfFirst { it.id == postId }
            if (index == -1) throw IllegalArgumentException("Post introuvable")

            val current = cache[index]
            val updated = current.copy(
                leftVotesCount = current.leftVotesCount + 1,
                totalVotesCount = current.totalVotesCount + 1,
                isVotedLeft = true,
                isVotedRight = false
            )

            cache[index] = updated
            updated
        }

    // 🔹 Vote droite : pareil, en mémoire
    override suspend fun voteRight(postId: String): Result<VsPost> =
        runCatching {
            if (cache.isEmpty()) {
                getFeed().getOrThrow()
            }

            val index = cache.indexOfFirst { it.id == postId }
            if (index == -1) throw IllegalArgumentException("Post introuvable")

            val current = cache[index]
            val updated = current.copy(
                rightVotesCount = current.rightVotesCount + 1,
                totalVotesCount = current.totalVotesCount + 1,
                isVotedLeft = false,
                isVotedRight = true
            )

            cache[index] = updated
            updated
        }
}

// 🧠 Mapper DTO -> Domaine
private fun PostDto.toDomain(): VsPost =
    VsPost(
        id = id,
        authorName = author_name ?: "Anonyme",
        authorAvatarUrl = author_avatar,
        category = category ?: "",
        createdAt = 0L, // pas utilisé pour l’instant
        question = question ?: "",
        leftImageUrl = left_image ?: "",
        rightImageUrl = right_image ?: "",
        leftLabel = left_label ?: "",
        rightLabel = right_label ?: "",
        leftVotesCount = left_votes,
        rightVotesCount = right_votes,
        totalVotesCount = left_votes + right_votes,
        isVotedLeft = false,
        isVotedRight = false
    )