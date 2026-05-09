package com.nazam.instaclone.feature.admin.domain.usecase

import com.nazam.instaclone.feature.admin.domain.model.AdminUser
import com.nazam.instaclone.feature.admin.domain.repository.AdminRepository

class ListUsersUseCase(
    private val repository: AdminRepository
) {
    suspend fun execute(
        query: String = "",
        page: Int = 0,
        pageSize: Int = 50,
    ): Result<List<AdminUser>> {
        return repository.listUsers(query, page, pageSize)
    }
}
