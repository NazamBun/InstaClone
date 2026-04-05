package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class GetPostByIdUseCase(
    private val repository: HomeRepository
) {
    suspend fun execute(postId: String): Result<VsPost> {
        return repository.getPostById(postId)
    }
}
