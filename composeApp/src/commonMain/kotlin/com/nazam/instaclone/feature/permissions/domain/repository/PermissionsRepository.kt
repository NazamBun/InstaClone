package com.nazam.instaclone.feature.permissions.domain.repository

import com.nazam.instaclone.feature.permissions.domain.model.PostPermission

/**
 * Source des permissions utilisateur (table post_permissions).
 */
interface PermissionsRepository {
    suspend fun getPostPermission(userId: String): Result<PostPermission>
    suspend fun isAdmin(userId: String): Result<Boolean>
}
