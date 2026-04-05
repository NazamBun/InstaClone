package com.nazam.instaclone.feature.home.presentation.ui.notifications

import com.nazam.instaclone.feature.home.domain.model.VsPost

object NotificationPostStore {
    private var post: VsPost? = null

    fun open(post: VsPost) {
        this.post = post
    }

    fun consume(): VsPost? {
        val value = post
        post = null
        return value
    }

    fun clear() {
        post = null
    }
}
