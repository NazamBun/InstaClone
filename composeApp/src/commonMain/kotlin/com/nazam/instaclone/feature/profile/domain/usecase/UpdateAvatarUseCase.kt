package com.nazam.instaclone.feature.profile.domain.usecase

import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository

class UpdateAvatarUseCase(
    private val repository: ProfileRepository
) {
    suspend fun execute(userId: String, avatarUrl: String): Result<Unit> {
        return repository.updateAvatar(userId, avatarUrl)
    }
}
