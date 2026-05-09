package com.nazam.instaclone.feature.admin.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminPostPermissionDto(
    @SerialName("user_id") val userId: String? = null,
    @SerialName("can_create_post") val canCreatePost: Boolean? = null,
)
