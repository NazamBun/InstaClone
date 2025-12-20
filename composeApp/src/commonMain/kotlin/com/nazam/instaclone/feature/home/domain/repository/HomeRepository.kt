package com.nazam.instaclone.feature.home.domain.repository

import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.domain.model.VsPost

interface HomeRepository {

    suspend fun getFeed(): Result<List<VsPost>>
    suspend fun voteLeft(postId: String): Result<VsPost>
    suspend fun voteRight(postId: String): Result<VsPost>

    suspend fun createPost(
        question: String,
        leftImageUrl: String,
        rightImageUrl: String,
        leftLabel: String,
        rightLabel: String,
        category: String
    ): Result<VsPost>

    // ✅ Comments
    suspend fun getComments(postId: String): Result<List<Comment>>
    suspend fun addComment(postId: String, content: String): Result<Comment>
}