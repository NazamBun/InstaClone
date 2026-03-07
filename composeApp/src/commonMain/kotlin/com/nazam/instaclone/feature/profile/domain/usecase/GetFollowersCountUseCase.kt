package com.nazam.instaclone.feature.profile.domain.usecase

import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository

class GetFollowersCountUseCase(
    private val repository: ProfileRepository
) {
    suspend fun execute(userId: String): Result<Int> {
        return repository.getFollowersCount(userId)
    }
}
