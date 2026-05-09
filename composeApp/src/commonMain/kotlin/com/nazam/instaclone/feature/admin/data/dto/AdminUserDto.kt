package com.nazam.instaclone.feature.admin.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminUserDto(
    @SerialName("id") val id: String? = null,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)
