package com.nazam.instaclone.feature.auth.domain.usecase

import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): Result<AuthUser> {
        return authRepository.login(email, password)
    }
}