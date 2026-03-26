package com.nazam.instaclone.feature.home.data.repository

import com.nazam.instaclone.feature.home.data.dto.CommentDto
import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.data.mapper.CommentMapper
import com.nazam.instaclone.feature.home.data.mapper.PostMapper
import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class HomeRepositoryImpl(
    private val client: SupabaseClient,
    private val json: Json
) : HomeRepository {

    private companion object {
        const val POSTS_FEED_VIEW = "posts_feed"
        const val POSTS_FOLLOWING_FEED_VIEW = "posts_following_feed"
        const val POSTS_TABLE = "posts"
        const val COMMENTS_TABLE = "comments"
    }

    override suspend fun getForYouFeedPage(offset: Int, limit: Int) = runCatching {
        fetchRecentPage(POSTS_FEED_VIEW, offset, limit)
    }

    override suspend fun getFollowingFeedPage(offset: Int, limit: Int) = runCatching {
        if (client.auth.currentUserOrNull() == null) emptyList()
        else fetchRecentPage(POSTS_FOLLOWING_FEED_VIEW, offset, limit)
    }

    override suspend fun getExplorePostsPage(offset: Int, limit: Int) = runCatching {
        fetchRecentPage(POSTS_FEED_VIEW, offset, limit)
    }

    override suspend fun createPost(
        question: String,
        leftImageUrl: String,
        rightImageUrl: String,
        leftLabel: String,
        rightLabel: String,
        category: String,
        selectedVsBadgeId: String
    ) = runCatching {
        val user = client.auth.currentUserOrNull()
            ?: throw IllegalStateException("AUTH_REQUIRED")

        val payload = CreatePostDto(
            question = question,
            category = category,
            leftImageUrl = leftImageUrl,
            rightImageUrl = rightImageUrl,
            leftLabel = leftLabel,
            rightLabel = rightLabel,
            selectedVsBadgeId = selectedVsBadgeId,
            authorName = user.email,
            authorAvatar = null
        )

        val response = client.postgrest[POSTS_TABLE].insert(payload) { select() }
        PostMapper.toDomain(decodePosts(response.data).first(), VoteChoice.NONE)
    }

    override suspend fun voteLeft(postId: String) = vote(postId, "left")

    override suspend fun voteRight(postId: String) = vote(postId, "right")

    override suspend fun getComments(postId: String) = runCatching {
        val response = client.postgrest[COMMENTS_TABLE].select {
            filter { eq("post_id", postId) }
            order(column = "created_at", order = Order.ASCENDING)
        }
        decodeComments(response.data).map(CommentMapper::toDomain)
    }

    override suspend fun addComment(postId: String, content: String) = runCatching {
        val user = client.auth.currentUserOrNull()
            ?: throw IllegalStateException("AUTH_REQUIRED")

        val payload = CreateCommentDto(
            postId = postId,
            authorId = user.id,
            content = content,
            authorName = user.email,
            authorAvatar = null
        )

        val response = client.postgrest[COMMENTS_TABLE].insert(payload) { select() }
        CommentMapper.toDomain(decodeComments(response.data).first())
    }

    private suspend fun vote(postId: String, choice: String) = runCatching {
        val user = client.auth.currentUserOrNull()
            ?: throw IllegalStateException("AUTH_REQUIRED")

        val response = client.postgrest.rpc(
            function = "vote_post",
            parameters = buildJsonObject {
                put("p_post_id", postId)
                put("p_user_id", user.id)
                put("p_choice", choice)
            }
        )

        val dto = decodePosts(response.data).first()
        val vote = if (choice == "left") VoteChoice.LEFT else VoteChoice.RIGHT
        PostMapper.toDomain(dto, vote)
    }

    private suspend fun fetchRecentPage(viewName: String, offset: Int, limit: Int): List<VsPost> {
        val safeOffset = offset.coerceAtLeast(0)
        val safeLimit = limit.coerceAtLeast(1)
        val to = safeOffset + safeLimit - 1

        val response = client.postgrest[viewName].select {
            range(from = safeOffset.toLong(), to = to.toLong())
            order(column = "created_at", order = Order.DESCENDING)
            order(column = "id", order = Order.DESCENDING)
        }

        return decodePosts(response.data).map(::mapPost)
    }

    private fun mapPost(dto: PostDto): VsPost {
        val userVote = when (dto.user_choice) {
            "left" -> VoteChoice.LEFT
            "right" -> VoteChoice.RIGHT
            else -> VoteChoice.NONE
        }
        return PostMapper.toDomain(dto, userVote)
    }

    private fun decodePosts(raw: String): List<PostDto> =
        json.decodeFromString(ListSerializer(PostDto.serializer()), raw)

    private fun decodeComments(raw: String): List<CommentDto> =
        json.decodeFromString(ListSerializer(CommentDto.serializer()), raw)

    @Serializable
    private data class CreatePostDto(
        val question: String,
        val category: String,
        @SerialName("left_image") val leftImageUrl: String,
        @SerialName("right_image") val rightImageUrl: String,
        @SerialName("left_label") val leftLabel: String,
        @SerialName("right_label") val rightLabel: String,
        @SerialName("vs_badge_id") val selectedVsBadgeId: String,
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
