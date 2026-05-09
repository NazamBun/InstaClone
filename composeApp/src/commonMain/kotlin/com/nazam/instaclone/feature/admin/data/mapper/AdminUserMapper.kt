package com.nazam.instaclone.feature.admin.data.mapper

import com.nazam.instaclone.feature.admin.data.dto.AdminUserDto
import com.nazam.instaclone.feature.admin.domain.model.AdminUser

object AdminUserMapper {
    fun toDomain(dto: AdminUserDto, canCreatePost: Boolean): AdminUser {
        return AdminUser(
            id = dto.id.orEmpty(),
            email = null,
            displayName = dto.displayName,
            canCreatePost = canCreatePost,
            createdAt = dto.createdAt,
        )
    }
}
