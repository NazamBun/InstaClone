package com.nazam.instaclone.feature.profile.domain.usecase

import com.nazam.instaclone.feature.profile.domain.model.Profile
import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository

class GetMyProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend fun execute(userId: String, emailFallback: String): Result<Profile> {
        return repository.getMyProfile(userId = userId, emailFallback = emailFallback)
    }
}
