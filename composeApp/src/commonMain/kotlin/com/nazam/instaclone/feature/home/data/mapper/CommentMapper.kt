package com.nazam.instaclone.feature.home.data.mapper

import com.nazam.instaclone.feature.home.data.dto.CommentDto
import com.nazam.instaclone.feature.home.domain.model.Comment

object CommentMapper {
    fun toDomain(dto: CommentDto): Comment {
        return Comment(
            id = dto.id,
            postId = dto.postId,
            authorId = dto.authorId,
            content = dto.content,
            authorName = dto.authorName,
            authorAvatarUrl = dto.authorAvatar,
            createdAt = dto.createdAt ?: ""
        )
    }
}