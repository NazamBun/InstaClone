package com.nazam.instaclone.feature.home.presentation.ui.explore

import com.nazam.instaclone.feature.home.domain.model.VsPost

object ExplorePagerStore {

    private var postIds: List<String> = emptyList()
    private var startPostId: String = ""
    private var fallbackPosts: List<VsPost> = emptyList()
    private var pendingCommentsPostId: String? = null

    fun open(
        postIds: List<String>,
        startPostId: String,
        fallbackPosts: List<VsPost> = emptyList(),
        pendingCommentsPostId: String? = null
    ) {
        this.postIds = postIds.distinct()
        this.startPostId = startPostId
        this.fallbackPosts = fallbackPosts
        this.pendingCommentsPostId = pendingCommentsPostId
    }

    fun getPostIds(): List<String> = postIds
    fun getStartPostId(): String = startPostId
    fun getFallbackPosts(): List<VsPost> = fallbackPosts

    fun consumePendingCommentsPostId(): String? {
        val value = pendingCommentsPostId
        pendingCommentsPostId = null
        return value
    }

    fun clear() {
        postIds = emptyList()
        startPostId = ""
        fallbackPosts = emptyList()
        pendingCommentsPostId = null
    }
}
