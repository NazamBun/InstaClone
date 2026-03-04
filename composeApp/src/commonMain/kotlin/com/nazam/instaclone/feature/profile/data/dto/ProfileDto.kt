package com.nazam.instaclone.feature.profile.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO Supabase (Postgrest).
 * Nullable pour éviter les crash si la DB change.
 */
@Serializable
data class ProfileDto(
    @SerialName("id") val id: String? = null,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,

    // ✅ optionnel : si ta table profiles a "created_at"
    @SerialName("created_at") val createdAt: String? = null
)
