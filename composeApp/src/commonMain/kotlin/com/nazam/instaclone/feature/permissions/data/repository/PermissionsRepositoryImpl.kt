package com.nazam.instaclone.feature.permissions.data.repository

import com.nazam.instaclone.core.utils.safeCall
import com.nazam.instaclone.feature.permissions.domain.model.PostPermission
import com.nazam.instaclone.feature.permissions.domain.repository.PermissionsRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class PermissionsRepositoryImpl(
    private val client: SupabaseClient,
    private val json: Json
) : PermissionsRepository {

    private companion object {
        const val TABLE = "post_permissions"
        const val TABLE_ADMINS = "admins"
        const val COL_USER_ID = "user_id"
        const val COL_CAN_CREATE_POST = "can_create_post"
    }

    override suspend fun getPostPermission(userId: String): Result<PostPermission> = safeCall {
        val response = client.postgrest[TABLE]
            .select {
                filter { eq(COL_USER_ID, userId) }
                limit(1)
            }

        val item = json.parseToJsonElement(response.data)
            .jsonArray
            .firstOrNull()
            ?.jsonObject
            ?: return@safeCall PostPermission.Denied

        val canCreate = item[COL_CAN_CREATE_POST]
            ?.jsonPrimitive
            ?.booleanOrNull == true

        PostPermission(canCreatePost = canCreate)
    }

    override suspend fun isAdmin(userId: String): Result<Boolean> = safeCall {
        val response = client.postgrest[TABLE_ADMINS]
            .select {
                filter { eq(COL_USER_ID, userId) }
                limit(1)
            }

        json.parseToJsonElement(response.data)
            .jsonArray
            .firstOrNull() != null
    }.recover { false }
}
