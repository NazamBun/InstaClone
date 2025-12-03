package com.nazam.instaclone.feature.auth.domain.usecase

import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository

class SignupUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String, displayName: String?): Result<AuthUser> {
        return authRepository.signup(email, password, displayName)
    }
}