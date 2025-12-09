package com.nazam.instaclone.feature.home.data.repository

import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.data.mapper.PostMapper
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class HomeRepositoryImpl(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : HomeRepository {

    // 📦 Noms des tables Supabase
    private val POSTS_TABLE = "posts"
    private val VOTES_TABLE = "votes"

    // 🔁 Json réutilisé
    private val json = Json {
        ignoreUnknownKeys = true
    }

    // 🧠 Cache en mémoire pour le feed
    private val cache = mutableListOf<VsPost>()

    // ----------------------------------------------------
    // 1) Charger le feed + votes de l’utilisateur
    // ----------------------------------------------------
    override suspend fun getFeed(): Result<List<VsPost>> =
        runCatching {
            // 1️⃣ On récupère tous les posts
            val postsResult: PostgrestResult = client
                .postgrest[POSTS_TABLE]
                .select()

            val postDtos: List<PostDto> = json.decodeFromString(
                deserializer = ListSerializer(PostDto.serializer()),
                string = postsResult.data
            )

            val basePosts: List<VsPost> = postDtos.map { PostMapper.toDomain(it) }

            // 2️⃣ On regarde si un user est connecté
            val user = client.auth.currentUserOrNull()
            if (user == null) {
                // Pas connecté → pas de vote utilisateur à appliquer
                cache.clear()
                cache.addAll(basePosts)
                return@runCatching basePosts
            }

            // 3️⃣ On essaie de charger les votes de l’utilisateur
            val postsWithUserVote: List<VsPost> = try {
                val votesResult: PostgrestResult = client
                    .postgrest[VOTES_TABLE]
                    .select()

                val voteDtos: List<VoteRowDto> = json.decodeFromString(
                    deserializer = ListSerializer(VoteRowDto.serializer()),
                    string = votesResult.data
                )

                // On garde uniquement les votes du user courant
                val votesByPostId: Map<String, VoteRowDto> = voteDtos
                    .filter { it.userId == user.id }
                    .associateBy { it.postId }

                // On "fusionne" posts + info de vote
                basePosts.map { post ->
                    val voteRow = votesByPostId[post.id]
                    if (voteRow == null) {
                        post // pas de vote pour ce post
                    } else {
                        val userVote = when (voteRow.choice) {
                            "left" -> VoteChoice.LEFT
                            "right" -> VoteChoice.RIGHT
                            else -> VoteChoice.NONE
                        }
                        post.copy(userVote = userVote)
                    }
                }
            } catch (e: Exception) {
                println("🔴 Erreur Supabase chargement votes : ${e.message}")
                basePosts
            }

            // 4️⃣ On met à jour le cache
            cache.clear()
            cache.addAll(postsWithUserVote)

            postsWithUserVote
        }

    // ----------------------------------------------------
    // 2) Vote gauche
    // ----------------------------------------------------
    override suspend fun voteLeft(postId: String): Result<VsPost> =
        vote(postId = postId, newChoice = VoteChoice.LEFT)

    // ----------------------------------------------------
    // 3) Vote droite
    // ----------------------------------------------------
    override suspend fun voteRight(postId: String): Result<VsPost> =
        vote(postId = postId, newChoice = VoteChoice.RIGHT)

    // ----------------------------------------------------
    // 4) Logique de vote commune
    // ----------------------------------------------------
    private suspend fun vote(
        postId: String,
        newChoice: VoteChoice
    ): Result<VsPost> =
        runCatching {
            if (cache.isEmpty()) {
                getFeed().getOrThrow()
            }

            val index = cache.indexOfFirst { it.id == postId }
            require(index != -1) { "Post introuvable" }

            val current = cache[index]
            val previous = current.userVote

            // 🔒 si on clique plusieurs fois sur la même photo → on ne change rien
            if (previous == newChoice) {
                return@runCatching current
            }

            // 🧮 applique la logique (incrémente / décrémente proprement)
            val updated = applyVoteLogic(
                current = current,
                previous = previous,
                newChoice = newChoice
            )

            // 1️⃣ cache
            cache[index] = updated

            // 2️⃣ update des compteurs dans `posts`
            syncPostCountersOnSupabase(updated)

            // 3️⃣ enregistrement du vote user dans `votes`
            persistUserVoteOnSupabase(
                postId = postId,
                choice = newChoice
            )

            updated
        }

    // ----------------------------------------------------
    // 5) Ajuster les compteurs en fonction du vote
    // ----------------------------------------------------
    private fun applyVoteLogic(
        current: VsPost,
        previous: VoteChoice,
        newChoice: VoteChoice
    ): VsPost {
        var left = current.leftVotesCount
        var right = current.rightVotesCount
        var total = current.totalVotesCount

        // 🔻 retirer l’ancien vote si besoin
        when (previous) {
            VoteChoice.LEFT -> {
                left = (left - 1).coerceAtLeast(0)
                total = (total - 1).coerceAtLeast(0)
            }
            VoteChoice.RIGHT -> {
                right = (right - 1).coerceAtLeast(0)
                total = (total - 1).coerceAtLeast(0)
            }
            VoteChoice.NONE -> Unit
        }

        // 🔺 ajouter le nouveau vote
        when (newChoice) {
            VoteChoice.LEFT -> {
                left += 1
                total += 1
            }
            VoteChoice.RIGHT -> {
                right += 1
                total += 1
            }
            VoteChoice.NONE -> Unit
        }

        return current.copy(
            leftVotesCount = left,
            rightVotesCount = right,
            totalVotesCount = total,
            userVote = newChoice
        )
    }

    // ----------------------------------------------------
    // 6) Sync des compteurs dans `posts`
    // ----------------------------------------------------
    private suspend fun syncPostCountersOnSupabase(post: VsPost) {
        try {
            client
                .postgrest[POSTS_TABLE]
                .upsert(
                    PostVotesUpdateDto(
                        id = post.id,
                        leftVotes = post.leftVotesCount,
                        rightVotes = post.rightVotesCount
                    )
                ) {
                    onConflict = "id"
                }
        } catch (e: Exception) {
            println("🔴 Erreur Supabase syncPostCountersOnSupabase postId=${post.id} : ${e.message}")
        }
    }

    // ----------------------------------------------------
    // 7) Enregistrer le vote user dans `votes`
    // ----------------------------------------------------
    private suspend fun persistUserVoteOnSupabase(
        postId: String,
        choice: VoteChoice
    ) {
        try {
            val user = client.auth.currentUserOrNull()
            val userId = user?.id ?: return

            client
                .postgrest[VOTES_TABLE]
                .upsert(
                    VoteRowDto(
                        postId = postId,
                        userId = userId,
                        choice = when (choice) {
                            VoteChoice.LEFT -> "left"
                            VoteChoice.RIGHT -> "right"
                            VoteChoice.NONE -> "none"
                        }
                    )
                ) {
                    onConflict = "post_id,user_id"
                }
        } catch (e: Exception) {
            println("🔴 Erreur Supabase persistUserVoteOnSupabase postId=$postId : ${e.message}")
        }
    }
}

// ----------------------------------------------------
// 8) DTOs pour les requêtes Supabase
// ----------------------------------------------------

@Serializable
private data class PostVotesUpdateDto(
    val id: String,
    @SerialName("left_votes") val leftVotes: Int,
    @SerialName("right_votes") val rightVotes: Int
)

@Serializable
private data class VoteRowDto(
    @SerialName("post_id") val postId: String,
    @SerialName("user_id") val userId: String,
    val choice: String
)