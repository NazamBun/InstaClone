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
    // On utilise le client Supabase partagé
    private val client: SupabaseClient = SupabaseClientProvider.client
) : HomeRepository {

    // ⚠️ Mets ici EXACTEMENT le nom de ta table Supabase
    // Exemple : "vs_posts" si ta table s'appelle comme ça
    private val TABLE = "posts"

    override suspend fun getFeed(): Result<List<VsPost>> =
        runCatching {
            // 1️⃣ Appel Supabase : SELECT * FROM vs_posts
            val result: PostgrestResult = client
                .postgrest[TABLE]
                .select()

            // 2️⃣ On décode le JSON en List<PostDto>
            val dtoList: List<PostDto> = Json {
                ignoreUnknownKeys = true // si Supabase renvoie des colonnes en plus, on s'en fiche
            }.decodeFromString(
                deserializer = ListSerializer(PostDto.serializer()),
                string = result.data
            )

            // 3️⃣ On convertit vers ton modèle de domaine VsPost
            dtoList.map { it.toDomain() }
        }

    // On branchera les vrais votes plus tard
    override suspend fun voteLeft(postId: String): Result<VsPost> =
        Result.failure(UnsupportedOperationException("Vote gauche pas encore implémenté"))

    override suspend fun voteRight(postId: String): Result<VsPost> =
        Result.failure(UnsupportedOperationException("Vote droite pas encore implémenté"))
}

// 🧠 Mapper DTO -> Domaine
private fun PostDto.toDomain(): VsPost =
    VsPost(
        id = id,
        authorName = author_name ?: "Anonyme",
        authorAvatarUrl = author_avatar,
        category = category ?: "",
        createdAt = 0L, // on ne l'affiche pas encore
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