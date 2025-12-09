package com.nazam.instaclone.feature.home.domain.repository

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
}