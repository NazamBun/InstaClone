package com.nazam.instaclone.feature.profile.domain.usecase

import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository

class IsFollowingUseCase(
    private val repository: ProfileRepository
) {
    suspend fun execute(
        followerId: String,
        followingId: String
    ): Result<Boolean> {
        return repository.isFollowing(
            followerId = followerId,
            followingId = followingId
        )
    }
}
