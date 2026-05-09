package com.nazam.instaclone.feature.permissions.domain.usecase

import com.nazam.instaclone.feature.permissions.domain.repository.PermissionsRepository

class IsAdminUseCase(
    private val repository: PermissionsRepository
) {
    suspend fun execute(userId: String): Result<Boolean> {
        return repository.isAdmin(userId)
    }
}
