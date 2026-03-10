package com.nazam.instaclone.core.access

import com.nazam.instaclone.feature.auth.domain.model.AuthUser

object CreatePostAccess {
    fun canCreate(user: AuthUser?): Boolean {
        return user?.canCreatePost == true
    }
}
