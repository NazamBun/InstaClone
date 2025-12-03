package com.nazam.instaclone.feature.auth.domain.usecase

import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(): Result<Unit> {
        return authRepository.logout()
    }
}