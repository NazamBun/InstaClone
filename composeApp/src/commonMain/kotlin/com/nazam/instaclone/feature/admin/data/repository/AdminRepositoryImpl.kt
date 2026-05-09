package com.nazam.instaclone.feature.admin.data.repository

import com.nazam.instaclone.core.utils.safeCall
import com.nazam.instaclone.feature.admin.data.dto.AdminPostPermissionDto
import com.nazam.instaclone.feature.admin.data.dto.AdminUserDto
import com.nazam.instaclone.feature.admin.data.mapper.AdminUserMapper
import com.nazam.instaclone.feature.admin.domain.model.AdminUser
import com.nazam.instaclone.feature.admin.domain.repository.AdminRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AdminRepositoryImpl(
    private val client: SupabaseClient,
    private val json: Json,
) : AdminRepository {

    private companion object {
        const val PROFILES_TABLE = "profiles"
        const val POST_PERMISSIONS_TABLE = "post_permissions"
        const val RPC_GRANT_POST_PERMISSION = "grant_post_permission"
        const val COL_DISPLAY_NAME = "display_name"
        const val COL_USER_ID = "user_id"
        const val COL_CREATED_AT = "created_at"
    }

    override suspend fun listUsers(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<List<AdminUser>> = safeCall {
        val from = (page * pageSize).toLong()
        val to = from + pageSize - 1

        val profilesResp = client.postgrest[PROFILES_TABLE].select {
            if (query.isNotBlank()) {
                filter { ilike(COL_DISPLAY_NAME, "%$query%") }
            }
            range(from, to)
            order(column = COL_CREATED_AT, order = Order.DESCENDING)
        }
        val profileDtos: List<AdminUserDto> = json.decodeFromString(
            ListSerializer(AdminUserDto.serializer()),
            profilesResp.data,
        )
        if (profileDtos.isEmpty()) return@safeCall emptyList()

        val ids = profileDtos.mapNotNull { it.id }
        if (ids.isEmpty()) {
            return@safeCall profileDtos.map { AdminUserMapper.toDomain(it, canCreatePost = false) }
        }

        val permsResp = client.postgrest[POST_PERMISSIONS_TABLE].select {
            filter { isIn(COL_USER_ID, ids) }
        }
        val permsByUser: Map<String, Boolean> = json
            .decodeFromString(ListSerializer(AdminPostPermissionDto.serializer()), permsResp.data)
            .associate { it.userId.orEmpty() to (it.canCreatePost == true) }

        profileDtos.map { dto ->
            AdminUserMapper.toDomain(
                dto = dto,
                canCreatePost = permsByUser[dto.id.orEmpty()] ?: false,
            )
        }
    }

    override suspend fun grantPostPermission(userId: String, allow: Boolean): Result<Unit> = safeCall {
        client.postgrest.rpc(
            function = RPC_GRANT_POST_PERMISSION,
            parameters = buildJsonObject {
                put("target_user_id", userId)
                put("allow", allow)
            },
        )
        Unit
    }
}
