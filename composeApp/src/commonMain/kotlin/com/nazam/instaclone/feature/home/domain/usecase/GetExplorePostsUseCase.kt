package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class GetExplorePostsUseCase(
    private val repository: HomeRepository
) {
    suspend fun execute(limit: Int = 120): Result<List<VsPost>> {
        return repository.getExplorePosts(limit)
    }
}
