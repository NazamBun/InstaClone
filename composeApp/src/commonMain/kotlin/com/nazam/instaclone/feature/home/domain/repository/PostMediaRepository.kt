package com.nazam.instaclone.feature.home.domain.repository

import com.nazam.instaclone.feature.home.domain.model.UploadProgress
import kotlinx.coroutines.flow.Flow

interface PostMediaRepository {
    fun uploadPostImage(localUri: String): Flow<UploadProgress>
}
