package com.nazam.instaclone.feature.profile.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileWriteDto(
    @SerialName("display_name") val displayName: String,
    @SerialName("username") val username: String,
    @SerialName("bio") val bio: String,
    @SerialName("location") val location: String,
    @SerialName("website") val website: String
)
