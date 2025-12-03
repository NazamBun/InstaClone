package com.nazam.instaclone.feature.auth.data.mapper

import com.nazam.instaclone.feature.auth.domain.model.AuthUser

object AuthMapper {

    fun toDomain(
        id: String,
        email: String?,
        displayName: String?
    ): AuthUser {
        return AuthUser(
            id = id,
            email = email ?: "",
            displayName = displayName
        )
    }
}