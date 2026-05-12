package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class CreateYesNoPostUseCase(
    private val homeRepository: HomeRepository
) {
    suspend fun execute(
        question: String,
        imageUrl: String
    ): Result<VsPost> {
        return homeRepository.createYesNoPost(
            question = question,
            imageUrl = imageUrl
        )
    }
}
