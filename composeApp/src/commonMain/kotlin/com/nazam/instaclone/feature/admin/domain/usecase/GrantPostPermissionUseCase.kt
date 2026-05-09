package com.nazam.instaclone.feature.admin.domain.usecase

import com.nazam.instaclone.feature.admin.domain.repository.AdminRepository

class GrantPostPermissionUseCase(
    private val repository: AdminRepository
) {
    suspend fun execute(userId: String): Result<Unit> {
        return repository.grantPostPermission(userId, allow = true)
    }
}
