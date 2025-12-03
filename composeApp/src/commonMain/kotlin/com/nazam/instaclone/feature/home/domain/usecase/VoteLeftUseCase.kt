package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class VoteLeftUseCase(
    private val repository: HomeRepository
) {
    suspend fun execute(postId: String): Result<VsPost> = repository.voteLeft(postId)
}