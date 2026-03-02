package com.nazam.instaclone.feature.home.domain.repository

import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.domain.model.VsPost

interface HomeRepository {

    // ✅ V2 : pagination
    suspend fun getFeedPage(offset: Int, limit: Int): Result<List<VsPost>>

    // ✅ compat (si tu en as besoin ailleurs)
    suspend fun getFeed(): Result<List<VsPost>> = getFeedPage(offset = 0, limit = 30)

    suspend fun createPost(
        question: String,
        leftImageUrl: String,
        rightImageUrl: String,
        leftLabel: String,
        rightLabel: String,
        category: String
    ): Result<VsPost>

    suspend fun voteLeft(postId: String): Result<VsPost>
    suspend fun voteRight(postId: String): Result<VsPost>

    suspend fun getComments(postId: String): Result<List<Comment>>
    suspend fun addComment(postId: String, content: String): Result<Comment>
}
