package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

/**
 * UseCase responsable de la création d’un post VS.
 * L’UI → ViewModel → UseCase → Repository
 */
class CreatePostUseCase(
    private val repository: HomeRepository
) {

    suspend fun execute(
        question: String,
        leftImageUrl: String,
        rightImageUrl: String,
        leftLabel: String,
        rightLabel: String,
        category: String
    ): Result<VsPost> {
        return repository.createPost(
            question = question,
            leftImageUrl = leftImageUrl,
            rightImageUrl = rightImageUrl,
            leftLabel = leftLabel,
            rightLabel = rightLabel,
            category = category
        )
    }
}