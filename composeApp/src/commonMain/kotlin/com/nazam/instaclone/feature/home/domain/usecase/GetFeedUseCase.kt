package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class GetFeedUseCase(
    private val repository: HomeRepository
) {
    suspend fun execute(): Result<List<VsPost>> = repository.getFeed()
}