package com.nazam.instaclone.feature.home.presentation.ui.explore

object ExplorePagerStore {

    private var postIds: List<String> = emptyList()
    private var startPostId: String = ""

    fun open(postIds: List<String>, startPostId: String) {
        this.postIds = postIds.distinct()
        this.startPostId = startPostId
    }

    fun getPostIds(): List<String> = postIds
    fun getStartPostId(): String = startPostId

    fun clear() {
        postIds = emptyList()
        startPostId = ""
    }
}
