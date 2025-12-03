package com.nazam.instaclone.feature.auth.domain.usecase

import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(): AuthUser? {
        return authRepository.getCurrentUser()
    }
}