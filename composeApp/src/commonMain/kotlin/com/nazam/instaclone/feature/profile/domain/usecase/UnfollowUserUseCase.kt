package com.nazam.instaclone.feature.profile.domain.usecase

import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository

class UnfollowUserUseCase(
    private val repository: ProfileRepository
) {
    suspend fun execute(
        followerId: String,
        followingId: String
    ): Result<Unit> {
        return repository.unfollow(
            followerId = followerId,
            followingId = followingId
        )
    }
}
