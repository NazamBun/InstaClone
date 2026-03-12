package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class GetExplorePostsUseCase(
    private val repository: HomeRepository
) {
    suspend fun execute(offset: Int, limit: Int): Result<List<VsPost>> {
        return repository.getExplorePostsPage(offset = offset, limit = limit)
    }
}
