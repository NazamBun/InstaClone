package com.nazam.instaclone.feature.home.data.repository

import com.nazam.instaclone.feature.home.data.dto.CommentDto
import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.data.mapper.CommentMapper
import com.nazam.instaclone.feature.home.data.mapper.PostMapper
import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.github.jan.supabase.postgrest.query.Order

class HomeRepositoryImpl(
    private val client: SupabaseClient,
    private val json: Json
) : HomeRepository {

    companion object {
        private const val POSTS_TABLE = "posts"
        private const val COMMENTS_TABLE = "comments"
    }

    // ----------------------------
    // POSTS
    // ----------------------------

    override suspend fun getFeed(): Result<List<VsPost>> =
        runCatching {
            val response = client
                .postgrest[POSTS_TABLE]
                .select()

            val dtos: List<PostDto> = json.decodeFromString(
                ListSerializer(PostDto.serializer()),
                response.data
            )

            dtos.map { PostMapper.toDomain(it) }
        }

    override suspend fun createPost(
        question: String,
        leftImageUrl: String,
        rightImageUrl: String,
        leftLabel: String,
        rightLabel: String,
        category: String
    ): Result<VsPost> =
        runCatching {
            val user = client.auth.currentUserOrNull()
                ?: throw IllegalStateException("AUTH_REQUIRED")

            val payload = CreatePostDto(
                question = question,
                category = category,
                leftImageUrl = leftImageUrl,
                rightImageUrl = rightImageUrl,
                leftLabel = leftLabel,
                rightLabel = rightLabel,
                authorName = user.email, // simple pour l’instant
                authorAvatar = null
            )

            val response = client
                .postgrest[POSTS_TABLE]
                .insert(payload) {
                    select()
                }

            val list: List<PostDto> = json.decodeFromString(
                ListSerializer(PostDto.serializer()),
                response.data
            )

            PostMapper.toDomain(list.first())
        }

    override suspend fun voteLeft(postId: String): Result<VsPost> =
        vote(postId, "LEFT")

    override suspend fun voteRight(postId: String): Result<VsPost> =
        vote(postId, "RIGHT")

    private suspend fun vote(postId: String, choice: String): Result<VsPost> =
        runCatching {
            val user = client.auth.currentUserOrNull()
                ?: throw IllegalStateException("AUTH_REQUIRED")

            val response = client
                .postgrest.rpc(
                    function = "vote_post",
                    parameters = buildJsonObject {
                        put("p_post_id", postId)
                        put("p_user_id", user.id)
                        put("p_choice", choice)
                    }
                )

            val list: List<PostDto> = json.decodeFromString(
                ListSerializer(PostDto.serializer()),
                response.data
            )

            PostMapper.toDomain(list.first())
        }

    // ----------------------------
    // COMMENTS
    // ----------------------------

    override suspend fun getComments(postId: String): Result<List<Comment>> =
        runCatching {
            val response = client
                .postgrest[COMMENTS_TABLE]
                .select {
                    filter {
                        eq("post_id", postId)
                    }
                    order(column = "created_at", order = Order.ASCENDING)
                }

            val dtos: List<CommentDto> = json.decodeFromString(
                ListSerializer(CommentDto.serializer()),
                response.data
            )

            dtos.map { CommentMapper.toDomain(it) }
        }

    override suspend fun addComment(
        postId: String,
        content: String
    ): Result<Comment> =
        runCatching {
            val user = client.auth.currentUserOrNull()
                ?: throw IllegalStateException("AUTH_REQUIRED")

            val payload = CreateCommentDto(
                postId = postId,
                authorId = user.id,
                content = content,
                authorName = user.email, // simple pour l’instant
                authorAvatar = null
            )

            val response = client
                .postgrest[COMMENTS_TABLE]
                .insert(payload) {
                    select()
                }

            val list: List<CommentDto> = json.decodeFromString(
                ListSerializer(CommentDto.serializer()),
                response.data
            )

            CommentMapper.toDomain(list.first())
        }

    // ----------------------------
    // DTO interne INSERT
    // ----------------------------

    @Serializable
    private data class CreatePostDto(
        val question: String,
        val category: String,
        @SerialName("left_image") val leftImageUrl: String,
        @SerialName("right_image") val rightImageUrl: String,
        @SerialName("left_label") val leftLabel: String,
        @SerialName("right_label") val rightLabel: String,
        @SerialName("author_name") val authorName: String? = null,
        @SerialName("author_avatar") val authorAvatar: String? = null
    )

    @Serializable
    private data class CreateCommentDto(
        @SerialName("post_id") val postId: String,
        @SerialName("author_id") val authorId: String,
        val content: String,
        @SerialName("author_name") val authorName: String? = null,
        @SerialName("author_avatar") val authorAvatar: String? = null
    )
}