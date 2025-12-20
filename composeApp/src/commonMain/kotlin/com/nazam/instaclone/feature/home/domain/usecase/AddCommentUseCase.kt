package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class AddCommentUseCase(
    private val repository: HomeRepository
) {
    suspend fun execute(postId: String, content: String): Result<Comment> =
        repository.addComment(postId, content)
}