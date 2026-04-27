package com.nazam.instaclone.feature.permissions.domain.usecase

import com.nazam.instaclone.feature.permissions.domain.model.PostPermission
import com.nazam.instaclone.feature.permissions.domain.repository.PermissionsRepository

class GetPostPermissionUseCase(
    private val repository: PermissionsRepository
) {
    suspend fun execute(userId: String): Result<PostPermission> {
        return repository.getPostPermission(userId)
    }
}
