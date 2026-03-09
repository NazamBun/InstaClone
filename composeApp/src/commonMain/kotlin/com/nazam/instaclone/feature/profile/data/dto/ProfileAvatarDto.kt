package com.nazam.instaclone.feature.profile.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileAvatarDto(
    @SerialName("avatar_url") val avatarUrl: String
)
