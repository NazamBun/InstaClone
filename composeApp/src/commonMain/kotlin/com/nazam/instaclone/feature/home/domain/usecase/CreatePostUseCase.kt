package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class CreatePostUseCase(
    private val homeRepository: HomeRepository
) {
    suspend fun execute(
        question: String,
        leftImageUrl: String,
        rightImageUrl: String,
        leftLabel: String,
        rightLabel: String,
        category: String
    ): Result<VsPost> {
        return homeRepository.createPost(
            question = question,
            leftImageUrl = leftImageUrl,
            rightImageUrl = rightImageUrl,
            leftLabel = leftLabel,
            rightLabel = rightLabel,
            category = category
        )
    }
}