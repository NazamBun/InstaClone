package com.nazam.instaclone.feature.home.domain.repository

import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.domain.model.VsPost

interface HomeRepository {
    suspend fun getForYouFeedPage(offset: Int, limit: Int): Result<List<VsPost>>
    suspend fun getFollowingFeedPage(offset: Int, limit: Int): Result<List<VsPost>>

    suspend fun getExplorePostsPage(offset: Int, limit: Int): Result<List<VsPost>>
    suspend fun getExplorePosts(limit: Int): Result<List<VsPost>> =
        getExplorePostsPage(offset = 0, limit = limit)

    suspend fun createPost(
        question: String,
        leftImageUrl: String,
        rightImageUrl: String,
        leftLabel: String,
        rightLabel: String,
        category: String,
        selectedVsBadgeId: String
    ): Result<VsPost>

    suspend fun voteLeft(postId: String): Result<VsPost>
    suspend fun voteRight(postId: String): Result<VsPost>

    suspend fun getComments(postId: String): Result<List<Comment>>
    suspend fun addComment(postId: String, content: String): Result<Comment>
}
