package com.nazam.instaclone.feature.auth.domain.repository

import com.nazam.instaclone.feature.auth.domain.model.AuthUser

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<AuthUser>

    suspend fun signup(email: String, password: String, displayName: String?): Result<AuthUser>

    suspend fun logout(): Result<Unit>

    suspend fun getCurrentUser(): AuthUser?
}