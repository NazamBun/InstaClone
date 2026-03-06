package com.nazam.instaclone.feature.profile.domain.usecase

import com.nazam.instaclone.feature.profile.domain.model.UpdateProfile
import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository

class UpdateMyProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend fun execute(userId: String, update: UpdateProfile): Result<Unit> {
        return repository.updateMyProfile(userId = userId, update = update)
    }
}
