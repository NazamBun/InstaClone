package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.FeedMode
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository

class GetFeedUseCase(
    private val repository: HomeRepository
) {
    suspend fun execute(
        mode: FeedMode,
        offset: Int,
        limit: Int
    ): Result<List<VsPost>> {
        return when (mode) {
            FeedMode.FOR_YOU -> repository.getForYouFeedPage(offset, limit)
            FeedMode.FOLLOWING -> repository.getFollowingFeedPage(offset, limit)
        }
    }
}
