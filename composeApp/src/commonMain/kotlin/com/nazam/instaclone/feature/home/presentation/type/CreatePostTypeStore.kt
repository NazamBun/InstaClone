package com.nazam.instaclone.feature.home.presentation.type

import com.nazam.instaclone.feature.home.presentation.model.CreatePostType

object CreatePostTypeStore {
    private var selectedType: CreatePostType = CreatePostType.DUEL

    fun set(type: CreatePostType) {
        selectedType = type
    }

    fun get(): CreatePostType = selectedType

    fun reset() {
        selectedType = CreatePostType.DUEL
    }
}
