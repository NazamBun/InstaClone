package com.nazam.instaclone.feature.admin.domain.repository

import com.nazam.instaclone.feature.admin.domain.model.AdminUser

interface AdminRepository {
    suspend fun listUsers(query: String = "", page: Int = 0, pageSize: Int = 50): Result<List<AdminUser>>
    suspend fun grantPostPermission(userId: String, allow: Boolean): Result<Unit>
}
