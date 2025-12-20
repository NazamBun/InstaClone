package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class GetCommentsUseCase(
    private val repository: HomeRepository
) {
    suspend fun execute(postId: String): Result<List<Comment>> = repository.getComments(postId)
}